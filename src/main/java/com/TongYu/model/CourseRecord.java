package com.TongYu.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 课时表
 *
 * @TableName course_record
 */
@TableName(value = "course_record")
@Data
public class CourseRecord implements Serializable {
    /**
     * 课时主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 预约时间-开始
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date startTime;

    /**
     * 预约时间-结束
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date endTime;

    /**
     * 经度
     */
    private String longitude;

    /**
     * 纬度
     */
    private String latitude;

    /**
     * 接送地址
     */
    private String address;

    /**
     * 预约备注
     */
    private String notes;

    /**
     * 课程性质
     */
    private String nature;

    /**
     * 课程内容
     */
    private String content;

    /**
     * 学员表现
     */
    private String performance;

    /**
     * 教练建议
     */
    private String proposal;

    /**
     * 课单状态
     */
    private String state;

    /**
     * 学员ID
     */
    private Long studentId;

    /**
     * 教练ID
     */
    private Long trainerId;

    /**
     * 教练ID
     */
    private String scheduleId;

    /**
     * 创建时间
     */
    private Date creatTime;

}