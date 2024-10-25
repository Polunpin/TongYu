package com.TongYu.service.impl;

import com.TongYu.aes.AesException;
import com.TongYu.aes.WXBizJsonMsgCrypt;
import com.TongYu.config.GlobalCache;
import com.TongYu.dto.JsSdkResponse;
import com.TongYu.model.Student;
import com.TongYu.service.StudentService;
import com.TongYu.service.WeComService;
import com.TongYu.util.ConstantUtil;
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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.sql.Timestamp;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        return JSON.parseObject(response.getBody());
    }

    @Override
    public Object getCallBack(HttpServletRequest request, String body) throws AesException {
        Map<String, String[]> parameterMap = request.getParameterMap();
        String jsonString = JSONObject.toJSONString(parameterMap);
        if (body == null) {//get
            log.info("1---GET--企业微信回调参数：{}", jsonString);
            return verificationUrl(request);
        } else {//post
            org.json.JSONObject bodyJson = XML.toJSONObject(body).getJSONObject("xml");
            log.info("2---POST--企业微信回调参数：{}, 解析参数：{}", jsonString, bodyJson);
            Map<String, String> resultMap = getRequestParameter(request, String.valueOf(bodyJson));
            //事件的类型|添加企业客户事件
            if (resultMap.get("Event").equals("change_external_contact") && resultMap.get("ChangeType").equals("add_external_contact")) {
                //添加客服，为企微注册用户
                log.info("2.1-----添加企业客户事件：{}", resultMap.get("ExternalUserID"));
                boolean externalUserID = registerStudent(resultMap.get("ExternalUserID"));
                log.info("2.2-----添加企业客户事件-注册结果：{}", externalUserID);
                return externalUserID;
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
    public Map<String, String> getRequestParameter(HttpServletRequest request, String body) throws AesException {
        log.info("=========参数解析开始=========");

        try {
            WXBizJsonMsgCrypt wxcpt = new WXBizJsonMsgCrypt("uxzZ3", encodingAESKey, corpId);

            String msgSignature = request.getParameter("msg_signature");
            String timeStamp = request.getParameter("timestamp");
            String nonce = request.getParameter("nonce");
            log.info("企业微信加密签名: {},时间戳: {},随机数: {}", msgSignature, timeStamp, nonce);
            String sMsg = wxcpt.DecryptMsg(msgSignature, timeStamp, nonce, body);
            Map<String, String> resultMap = new HashMap<>(16);
            //TODO 解析参数
            resultMap = ConstantUtil.parseXmlToMap(sMsg, resultMap);
            log.info("decrypt密文转为map结果为{}", resultMap);
            log.info("=========参数解析结束=========");
            return resultMap;
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
        QueryWrapper<Student> wrapper = new QueryWrapper<>();
        wrapper.eq("external_user_id", externalUserId);
        //先查询，存在时不保存（防止删除、重复添加行为）
        Student studentRes = studentService.getOne(wrapper);
        if (studentRes == null) {
            Student student = new Student();
            student.setExternalUserId(externalUserId);
            JSONObject wxCustomerDetails = getWxCustomerDetails(externalUserId);

            log.info("用户注册--企业微信客户详情：{}", wxCustomerDetails);
            student.setUnionId(wxCustomerDetails.getJSONObject("external_contact").getString("unionid"));
            student.setGender(wxCustomerDetails.getJSONObject("external_contact").getString("gender"));
            student.setAddWay(wxCustomerDetails.getJSONArray("follow_user").getJSONObject(0).getString("add_way"));
            student.setChannel(wxCustomerDetails.getJSONArray("follow_user").getJSONObject(0).getString("state"));
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

    @SneakyThrows
    @Override
    public void getLoginUrl(HttpServletResponse response, String redirectURI) {
        String stateKey = "WWLogin" + RandomString.make(6);
        GlobalCache.put(stateKey, 1);

        // Step 3: 构造授权请求URL
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>(1);
        //定义query参数
        params.add("login_type", "CorpApp");
        params.add("appid", corpId);
        params.add("agentid", "1000006");
        params.add("state", stateKey);
        params.add("redirect_uri", "https://web.goldenguard.top/weCom/loginCallBack");
        //定义url参数
        String url = UriComponentsBuilder.
                fromUriString("https://login.work.weixin.qq.com/wwlogin/sso/login").queryParams(params).toUriString();
        response.sendRedirect(url);
    }

    @Override
    public String loginCallBack(String code, String state) {
        // 验证state是否匹配
        if ("1".equals(GlobalCache.get(state))) {
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
}


