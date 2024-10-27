package com.TongYu.service.impl;

import com.TongYu.aes.AesException;
import com.TongYu.aes.WXBizJsonMsgCrypt;
import com.TongYu.config.GlobalCache;
import com.TongYu.dto.JsSdkResponse;
import com.TongYu.model.Student;
import com.TongYu.service.StudentService;
import com.TongYu.service.WeComService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.utility.RandomString;
import org.json.XML;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Resource;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.sql.Timestamp;
import java.util.Base64;
import java.util.Date;

/**
 * @author lanyiping
 * @description 针对表【student(学员表)】的数据库操作Service实现
 * @createDate 2024-10-11 00:50:56
 */
@Slf4j
@Service
public class WeComServiceImpl implements WeComService {


    @Value("${wx.space-id}")
    private String spaceId;
    @Value("${wx.father-id}")
    private String fatherId;
    @Value("${wx.corpId}")
    private String corpId;
    @Value("${wx.corpSecret}")
    private String corpSecret;
    @Value("${wx.encodingAESKey}")
    private String encodingAESKey;
    @Resource
    public StudentService studentService;


    @Override
    @SneakyThrows
    public String uploadFileToWeCom(MultipartFile file, String stuId) {
        //获取文件名的后缀
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.lastIndexOf(".") != -1) {
            extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        }
        //获取学员信息,用于文件名标注学员姓名
        Student student = studentService.getById(stuId);
        //上传文件到企业微信
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://qyapi.weixin.qq.com/cgi-bin/wedrive/file_upload?access_token=" + GlobalCache.get("access_token");
        JSONObject jsonObject = new JSONObject();
        //文件内容
        jsonObject.put("file_base64_content", Base64.getEncoder().encodeToString(file.getBytes()));
        //学员姓名+文件名后缀
        jsonObject.put("file_name", student.getStuName() + "." + extension);
        //空间ID（固定值）
        jsonObject.put("spaceid", spaceId);
        jsonObject.put("fatherid", fatherId);
        //POST请求
        ResponseEntity<String> response = restTemplate.postForEntity(url, jsonObject, String.class);
        JSONObject object = JSONObject.parseObject(response.getBody());
        if (object.getIntValue("errcode") == 0) {
            return object.getString("fileid");
        } else {
            log.info("上传文件到企业微信失败！返回结果：{}", response.getBody());
        }
        return response.getBody();
    }

    @Override
    public void getCorpAccessToken() {
        GlobalCache.remove("access_token");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>(2);
        //定义query参数
        params.add("corpid", corpId);
        params.add("corpsecret", corpSecret);
        //定义url参数
        String url = UriComponentsBuilder.fromUriString("https://qyapi.weixin.qq.com/cgi-bin/gettoken").queryParams(params).toUriString();
        RestTemplate restTemplate = new RestTemplate();
        //获取access_token,get请求
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        // 将响应体转换为 JSONObject
        JSONObject object = JSONObject.parseObject(response.getBody());
        // 缓存access_token
        GlobalCache.put("access_token", object.get("access_token"));
        log.info("更新AccessToken成功，有效期{}秒；access_token：{}", object.get("expires_in"), object.get("access_token"));
    }

    @Override
    public String jsCode2session(String jsCode) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>(4);
        //定义query参数
        params.add("appid", "wx5745b3f0a911e8d8");
        params.add("secret", "9a4e5d01d729f1b9951b7d37a3375da0");
        params.add("js_code", jsCode);
        params.add("grant_type", "authorization_code");
        //定义url参数
        String url = UriComponentsBuilder.fromUriString("https://api.weixin.qq.com/sns/jscode2session").queryParams(params).toUriString();
        //请求小程序API
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        return response.getBody();
    }

    @Override
    public JSONObject getWxCustomerDetails(String externalUserId) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>(2);
        //定义query参数
        params.add("access_token", String.valueOf(GlobalCache.get("access_token")));
        params.add("external_userid", externalUserId);
        //定义url参数
        String url = UriComponentsBuilder.fromUriString("https://qyapi.weixin.qq.com/cgi-bin/externalcontact/get").queryParams(params).toUriString();
        //请求企业微信API
        return JSON.parseObject(new RestTemplate().getForEntity(url, String.class).getBody());
    }

    @Override
    public Object getCallBack(HttpServletRequest request, String body) throws AesException {
        String jsonString = JSONObject.toJSONString(request.getParameterMap());
        if (body == null) {//get
            log.info("1---GET--企业微信回调参数：{}", jsonString);
            return verificationUrl(request);
        } else {//post
            log.info("2---POST--企业微信回调参数：{}", jsonString);
            org.json.JSONObject resultJson = getRequestParameter(request, String.valueOf(XML.toJSONObject(body).getJSONObject("xml")));
            //事件类型
            if (resultJson.get("Event").equals("change_external_contact") && resultJson.get("ChangeType").equals("add_external_contact")) {
                //添加企业客户事件-注册学员信息
                return registerStudent(resultJson.getString("ExternalUserID"));
            }
            //支付和退款回调时这个值均为：encrypt-resource
            if (resultJson.get("resource_type").equals("encrypt-resource")) {
                log.info("3---POST--支付退款回调信息：{}", resultJson.getString("encrypt"));
                //更新学员信息
//                return updateStudentInfoByPayNotify(String.valueOf(resultJson));
            }
        }
        return null;
    }


    /**
     * 验证回调URL
     */
    public Object verificationUrl(HttpServletRequest request) throws AesException {
        log.info("===验证URL有效性开始");
        String sEchoStr; //需要返回的明文
        try {
            WXBizJsonMsgCrypt wxcpt = new WXBizJsonMsgCrypt("uxzZ3", encodingAESKey, corpId);

            String msgSignature = request.getParameter("msg_signature");
            String timeStamp = request.getParameter("timestamp");
            String nonce = request.getParameter("nonce");
            String echostr = request.getParameter("echostr");
            sEchoStr = wxcpt.VerifyURL(msgSignature, timeStamp, nonce, echostr);
            log.info("===验证URL有效性结束");
            return sEchoStr;
        } catch (AesException e) {
            log.error("验证URL失败，错误原因请查看异常:{}", e.getCode());
            throw new AesException(e.getCode());
        }
    }

    /**
     * 企业微信回调参数解析
     */
    public org.json.JSONObject getRequestParameter(HttpServletRequest request, String body) throws AesException {
        try {
            String sMsg = new WXBizJsonMsgCrypt("uxzZ3", encodingAESKey, corpId).DecryptMsg(
                    request.getParameter("msg_signature"),
                    request.getParameter("timestamp"),
                    request.getParameter("nonce"), body);
            return XML.toJSONObject(sMsg);
        } catch (AesException e) {
            log.error("密文参数解析失败，错误原因请查看异常:{}", e.getMessage());
            throw new AesException(e.getCode());
        }
    }

    /**
     * 用户注册
     * 1. 查询企业微信用户
     * 2. 注册学员信息
     * 3. 关联学员与企业微信用户的关系 unionId--external_userid
     *
     * @param externalUserId 外部联系人ID
     * @return 注册结果
     */
    public boolean registerStudent(String externalUserId) {
        //先查询，存在时不保存（防止删除、重复添加行为）
        Student studentRes = studentService.getOne(new QueryWrapper<Student>().eq("external_user_id", externalUserId));
        if (studentRes == null) {
            Student student = new Student();
            student.setExternalUserId(externalUserId);
            JSONObject wxCustomer = getWxCustomerDetails(externalUserId);
            student.setUnionId(wxCustomer.getJSONObject("external_contact").getString("unionid"));
            student.setGender(wxCustomer.getJSONObject("external_contact").getString("gender"));
            student.setAddWay(wxCustomer.getJSONArray("follow_user").getJSONObject(0).getString("add_way"));
            student.setChannel(wxCustomer.getJSONArray("follow_user").getJSONObject(0).getString("state"));
            //注册学员信息-学员与企业微信用户的关系关联
            return studentService.save(student);
        }
        return false;
    }

    @SneakyThrows
    @Override
    public JsSdkResponse getJsConfig(String pageUrl) {
        //生成签名的时间戳
        long timestamp = new Timestamp(new Date().getTime()).getTime();
        //生成签名的随机串
        String nonceStr = RandomString.make(16);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>(1);
        //定义query参数
        params.add("access_token", String.valueOf(GlobalCache.get("access_token")));
        //定义url参数
        String url = UriComponentsBuilder.fromUriString("https://qyapi.weixin.qq.com/cgi-bin/ticket/get?type=agent_config").queryParams(params).toUriString();
        RestTemplate restTemplate = new RestTemplate();
        //获取access_token,get请求
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        // 将响应体转换为 JSONObject
        JSONObject jsonObject = JSONObject.parseObject(response.getBody());
        String ticket = jsonObject.getString("ticket");

        //签名，见 附录-JS-SDK使用权限签名算法
        String signature = "jsapi_ticket=" + ticket + "&noncestr=" + nonceStr + "&timestamp=" + timestamp + "&url=" + URLDecoder.decode(pageUrl, "UTF-8");
        byte[] digest = MessageDigest.getInstance("SHA-1").digest(signature.getBytes());
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : digest) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                stringBuilder.append('0');
            }
            stringBuilder.append(hex);
        }
        JsSdkResponse jsSdkResponse = new JsSdkResponse();
        jsSdkResponse.setCorpId(corpId);
        jsSdkResponse.setAgentId("1000006");
        jsSdkResponse.setTimestamp(timestamp);
        jsSdkResponse.setNonceStr(nonceStr);
        jsSdkResponse.setSignature(stringBuilder.toString());
        return jsSdkResponse;
    }

    @Override
    public String getUserInfo(String code, String state) {
        log.info("state:{}, code:{}", state, code);
        // 验证state是否匹配
        if ("GoldenGuard".equals(state)) {
            // 通过验证，可以继续处理授权结果
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>(1);
            //定义query参数
            params.add("access_token", String.valueOf(GlobalCache.get("access_token")));
            params.add("code", code);
            String url = UriComponentsBuilder.
                    fromUriString("https://qyapi.weixin.qq.com/cgi-bin/auth/getuserinfo").queryParams(params).toUriString();
            //请求企业微信API
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return response.getBody();
        } else {
            log.error("CSRF OR ERROR 检测到攻击或无效状态!");
        }
        return null;
    }

    @SneakyThrows
    @Override
    public Object updateStudentInfoByPayNotify(String body) {
        JSONObject jsonObject = new JSONObject(Boolean.parseBoolean(body));

        // 1. 获取callback_aeskey（Base64编码的AES密钥）
        String callbackAesKey = "你的callback_aeskey"; // 替换为你的AES密钥
        byte[] aesKey = Base64.getDecoder().decode(callbackAesKey);

        // 2. 获取nonce和associated_data
        byte[] nonce = "随机生成的nonce".getBytes(); // 替换为实际生成的nonce
        byte[] associatedData = "associated_data".getBytes(); // 替换为实际的associated_data

        // 3. 进行Base64解码
        String base64Ciphertext = "资源中的ciphertext"; // 替换为你的Base64编码的密文
        byte[] ciphertext = Base64.getDecoder().decode(base64Ciphertext);

        // 4. 使用AES密钥、nonce和associated_data进行解密
        String decryptedData = decrypt(aesKey, nonce, associatedData, ciphertext);
        System.out.println("解密后的数据: " + decryptedData);

        jsonObject.getJSONObject("resource").getJSONObject("ciphertext");
        //支付成功事件
        if ("TRANSACTION.SUCCESS".equals(jsonObject.getString("event_type"))) {
            jsonObject.getJSONObject("resource").getJSONObject("payment").getString("transaction_id");
            //根据transaction_id更新学员购买课时
        }
        //支付失败事件
        if ("REFUND.SUCCESS".equals(jsonObject.getString("event_type"))) {

        }
        //根据支付信息更新学员购买课时

        return null;
    }

    private static String decrypt(byte[] key, byte[] nonce, byte[] associatedData, byte[] ciphertext) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(16 * 8, nonce); // 128-bit tag
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, gcmParameterSpec);
        cipher.updateAAD(associatedData); // 添加附加数据

        byte[] decryptedBytes = cipher.doFinal(ciphertext);
        return new String(decryptedBytes); // 返回解密后的字符串
    }
}


