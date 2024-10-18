package com.TongYu.service.impl;

import com.TongYu.config.GlobalCache;
import com.TongYu.model.Student;
import com.TongYu.service.StudentService;
import com.TongYu.service.WeComService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.websocket.server.PathParam;
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
        ResponseEntity<String> response = restTemplate.postForEntity(url, jsonObject.toString(), String.class);
        JSONObject object = new JSONObject(response.getBody());
        if (object.getInt("errcode") == 0) {
            return object.getString("fileid");
        } else {
            log.info("上传文件到企业微信失败！返回结果：{}", response.getBody());
        }
        return response.getBody();
    }

}


