package com.TongYu.controller;

import com.TongYu.config.GlobalCache;
import com.TongYu.service.WeComService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * 企微相关接口
 *
 * @author TongYu
 */
@Slf4j
@RestController()
@RequestMapping("/courseRecord")
public class WeComController {

    @Resource
    public WeComService weComService;

    @PostMapping("/uploadFile")
    public String uploadFile(@RequestPart("file") MultipartFile file, @RequestPart("stuId") String stuId) {
        // 驾驶证图片保存至企业微信微盘
        return weComService.uploadFileToWeCom(file, stuId);
    }

    /**
     * 更新AccessToken
     * 每两个小时执行一次
     */
    @Scheduled(cron = "0 0 */2 * * ?")
    public void getCorpAccessToken() throws JSONException {
        GlobalCache.remove("access_token");
        //上传文件到企业微信
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=wwcb10560218a47a01&corpsecret=7pceCMURfdDHC0iXvKY9JIE-GRSVzvd-25pjxIqom9g";
        //获取access_token,get请求
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        JSONObject object = new JSONObject(response.getBody());
        GlobalCache.put("access_token", object.get("access_token"));
        log.info("更新AccessToken成功，有效期{}秒；access_token：{}", object.get("expires_in"), object.get("access_token"));
    }

//    /**
//     * jsCode2session|获取 session_key
//     * @param js_code 前端获取的 code
//     */
//    @PostMapping("/jsCode2session")
//    public ApiResponse jsCode2session(String js_code) throws JSONException {
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("appid", "wx3e5902a3e5f51155");
//        jsonObject.put("secret", "f2c766e689b40d297115cdf7a246e8f7");
//        jsonObject.put("js_code", js_code);
//        jsonObject.put("grant_type", "'authorization_code'");
//        RestTemplate restTemplate = new RestTemplate();
//        String url = "https://api.weixin.qq.com/sns/jscode2session";
//        ResponseEntity<String> response = restTemplate.postForEntity(url, jsonObject.toString(), String.class);
//        return ApiResponse.ok(response.getBody());
//    }

}