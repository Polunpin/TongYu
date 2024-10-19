package com.TongYu.service;

import com.TongYu.config.ApiResponse;
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
     * @param stuId 学生ID
     * @return void
     */
    String uploadFileToWeCom(MultipartFile file, String stuId);

    /**
     * 更新AccessToken
     * 每两个小时执行一次
     */
    void getCorpAccessToken();

    /**
     * jsCode2session|获取 session_key
     *
     * @param jsCode 前端获取的 code
     * @return String
     */
    String jsCode2session(String jsCode);

    /**
     * 获取微信客户详情
     * @param unionId 微信客户unionId
     */
    Object getWxCustomerDetails(String unionId);

}
