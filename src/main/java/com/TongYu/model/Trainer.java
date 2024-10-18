package com.TongYu.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 教练表
 * @TableName trainer
 */
@Data
@TableName(value ="trainer")
public class Trainer implements Serializable {
    /**
     * 教练主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 企业微信用户ID
     */
    private String workUserId;

    /**
     * 教练姓名
     */
    private String trainerName;

    /**
     * 手机号
     */
    private String telephone;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 课时费用
     */
    private int trainerPrice;

    /**
     * 车牌
     */
    private String numberplate;

    /**
     * 车辆
     */
    private String car;

    /**
     * 省份
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 区域
     */
    private String area;

    /**
     * 在职状态
     */
    private Integer trainerStatus;

    /**
     * 创建时间
     */
    private Date creatTime;

}