package com.TongYu.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 学员表
 * @TableName student
 */
@TableName(value ="student")
@Data
public class Student implements Serializable {
    /**
     * 学员主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 微信全平台统一ID
     */
    private String unionId;

    /**
     * 微信各个主体ID
     */
    private String openId;

    /**
     * 外部联系人的userid
     */
    private String externalUserId;

    /**
     * 教练ID
     */
    private Long trainerId;

    /**
     * 学员姓名
     */
    private String stuName;

    /**
     * 性别
     */
    private String gender;

    /**
     * 手机号
     */
    private String telephone;

    /**
     * 驾驶证
     */
    private String image;

    /**
     * 来源
     */
    private String addWay;

    /**
     * 渠道
     */
    private String channel;

    /**
     * 总课时
     */
    private String externalUserName;

    /**
     * 赠送课时
     */
    private Integer give;

    /**
     * 剩余课时（默认总课时）
     */
    private Integer lave;

    /**
     * 已预约课时
     */
    private Integer used;

    /**
     * 意向度
     */
    private Integer intention;

    /**
     * 意向度分析
     */
    private String analysis;

    /**
     * 跟进状态
     */
    private String stage;

    /**
     * 创建时间
     */
    private Date creatTime;

}