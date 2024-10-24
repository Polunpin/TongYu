package com.TongYu.dto;

import com.TongYu.model.Student;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 课单列表-学员信息
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class StudentCourseResponse extends Student {

    /**
     * 距离上次练车时间
     */
    private Date lastTrainTime;

    /**
     * 距离上次练车时间天数
     */
    private String lastTrainDays;


}
