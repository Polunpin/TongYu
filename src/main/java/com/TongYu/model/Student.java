package com.TongYu.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

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
    private Object gender;

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
    private Object channel;

    /**
     * 总课时
     */
    private Integer total;

    /**
     * 赠送课时
     */
    private Integer give;

    /**
     * 剩余课时
     */
    private Integer lave;

    /**
     * 已用课时
     */
    private Integer used;

    /**
     * 咨询日期
     */
    private Date consultDate;

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
    private Object stage;

    /**
     * 创建时间
     */
    private Date creatTime;

}