package com.TongYu.dto;

import com.TongYu.model.CourseRecord;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 课时列表返回对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CourseResponse extends CourseRecord {

    /**
     * 教练姓名
     */
    private String trainerName;

    /**
     * 预约日期
     */
    private String appointmentDate;

    /**
     * 预约时间
     */
    private String appointmentTime;

}
