package com.TongYu.controller;

import com.TongYu.config.ApiResponse;
import com.TongYu.config.GlobalCache;
import com.TongYu.service.WeComService;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Resource;

/**
 * 企微相关接口
 *
 * @author TongYu
 */
@Slf4j
@RestController()
@RequestMapping("/weCom")
public class WeComController {

    @Resource
    public WeComService weComService;

    /**
     * 上传驾驶证图片至企业微信微盘
     * @param file 驾驶证图片
     * @param stuId 学生id
     */
    @PostMapping("/uploadFile")
    public ApiResponse uploadFile(@RequestPart("file") MultipartFile file, @RequestPart("stuId") String stuId) {
        // 驾驶证图片保存至企业微信微盘
        return ApiResponse.ok(weComService.uploadFileToWeCom(file, stuId));
    }

    /**
     * 更新AccessToken
     * 每两个小时执行一次
     */
    @Scheduled(cron = "0 0 */2 * * ?")
    public void getCorpAccessToken() {
        weComService.getCorpAccessToken();
    }

    /**
     * jsCode2session|获取 session_key
     * @param js_code 前端获取的 code
     */
    @PostMapping("/jsCode2session")
    public ApiResponse jsCode2session(String js_code) {
        return ApiResponse.ok(weComService.jsCode2session(js_code));
    }

    /**
     * 获取微信客户详情
     * @param unionId 微信客户unionId
     */
    @GetMapping("/getWxCustomerDetails")
    public ApiResponse getWxCustomerDetails(String unionId) {
        return ApiResponse.ok(weComService.getWxCustomerDetails(unionId));
    }
}