package com.TongYu.controller;

import com.TongYu.config.ApiResponse;
import com.TongYu.model.CourseRecord;
import com.TongYu.service.WeComService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * 企微相关接口
 * @author TongYu
 * @date 2024/10/14 01:46
 */
@Slf4j
@RestController()
@RequestMapping("/courseRecord")
public class WeComController {

    @Resource
    public WeComService weComService;

    @PostMapping("/uploadFile")
    public String uploadFile(@RequestPart("file") MultipartFile file,@RequestPart("stuId") String stuId) {
        // 驾驶证图片保存至企业微信微盘
        return weComService.uploadFileToWeCom(file,stuId);
    }

}
