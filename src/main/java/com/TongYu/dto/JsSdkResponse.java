package com.TongYu.dto;

import lombok.Data;

/**
 * JS-SDK 返回参数
 */
@Data
public class JsSdkResponse {
    /**
     * 企业微信的corpID
     */
    private String corpId;

    /**
     * 企业微信的应用id
     */
    private String agentId;

    /**
     * 生成签名的时间戳
     */
    private Long timestamp;
    /**
     * 生成签名的随机串
     */
    private String nonceStr;
    /**
     * 签名
     */
    private String signature;

}
