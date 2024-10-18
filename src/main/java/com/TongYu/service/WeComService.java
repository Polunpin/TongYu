package com.TongYu.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author lanyiping
 * @description 企业微信API接口
 * @createDate 2024-10-13 00:50:56
 */
public interface WeComService {

    /**
     * 上传文件到企业微信
     *
     * @param file  文件
     * @param stuId
     * @return void
     */
    String uploadFileToWeCom(MultipartFile file, String stuId);
}
