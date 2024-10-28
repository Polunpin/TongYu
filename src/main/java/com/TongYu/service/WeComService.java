package com.TongYu.service;

import com.TongYu.aes.AesException;
import com.TongYu.dto.JsSdkResponse;
import com.alibaba.fastjson.JSONObject;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

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
     * @param externalUserId 微信外部联系人ID
     */
    JSONObject getWxCustomerDetails(String externalUserId);

    Object getCallBack(HttpServletRequest request, String body) throws AesException;

    boolean registerStudent(String externalUserId);

    JsSdkResponse getJsConfig(String pageUrl);

    String getUserInfo(String code, String state);

    Object createCalendar(String info);
}
