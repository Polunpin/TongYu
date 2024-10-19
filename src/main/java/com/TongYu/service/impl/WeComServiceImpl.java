package com.TongYu.service.impl;

import com.TongYu.config.ApiResponse;
import com.TongYu.config.GlobalCache;
import com.TongYu.model.Student;
import com.TongYu.service.StudentService;
import com.TongYu.service.WeComService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Resource;
import java.util.Base64;

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
        String url = "https://qyapi.weixin.qq.com/cgi-bin/wedrive/file_upload?access_token="+ GlobalCache.get("access_token");
        JSONObject jsonObject = new JSONObject();
        //文件内容
        jsonObject.put("file_base64_content", Base64.getEncoder().encodeToString(file.getBytes()));
        //学员姓名+文件名后缀
        jsonObject.put("file_name", student.getStuName()+"."+extension);
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
        params.add("corpid", "wwcb10560218a47a01");
        params.add("corpsecret", "7pceCMURfdDHC0iXvKY9JIE-GRSVzvd-25pjxIqom9g");
        //定义url参数
        String url = UriComponentsBuilder.fromUriString("https://qyapi.weixin.qq.com/cgi-bin/gettoken")
                .queryParams(params).toUriString();
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
    public String jsCode2session(String js_code) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>(4);
        //定义query参数
        params.add("appid", "wx3e5902a3e5f51155");
        params.add("secret", "f2c766e689b40d297115cdf7a246e8f7");
        params.add("js_code", js_code);
        params.add("grant_type", "authorization_code");
        //定义url参数
        String url = UriComponentsBuilder.fromUriString("https://api.weixin.qq.com/sns/jscode2session")
                .queryParams(params).toUriString();
        //请求小程序API
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        return response.getBody();
    }

    @Override
    public JSONObject getWxCustomerDetails(String unionId) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>(2);
        //定义query参数
        params.add("access_token", String.valueOf(GlobalCache.get("access_token")));
        params.add("external_userid", unionId);
        //定义url参数
        String url = UriComponentsBuilder.fromUriString("https://qyapi.weixin.qq.com/cgi-bin/externalcontact/get")
                .queryParams(params).toUriString();
        //请求企业微信API
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        return JSON.parseObject(response.getBody());
    }
}


