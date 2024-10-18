package com.TongYu.dto;

import lombok.Data;

@Data
public class TrainerResponse {

    /**
     * 教练ID
     */
    private Long trainerId;
    /**
     * 企业微信用户ID
     */
    private String unionId;
    /**
     * 手机号
     */
    private String telephone;
    /**
     * 车牌
     */
    private String numberplate;
    /**
     * 车牌|是否限行
     */
    private String isRestriction;
    /**
     * 课时费用
     */
    private String trainerPrice;
    /**
     * 教练名称
     */
    private String trainerName;
    /**
     * 体验课X节
     */
    private Integer trialClass;
    /**
     * 正式课Y节
     */
    private Integer formalClass;
    /**
     * 本月服务学员
     */
    private Integer total;

    /**
     * 教练在职状态
     */
    private Integer trainerStatus;

}
