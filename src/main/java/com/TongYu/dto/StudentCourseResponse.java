package com.TongYu.dto;

import com.TongYu.model.Student;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 课单列表-学员信息
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class StudentCourseResponse extends Student {

    /**
     * 距离上次练车时间
     */
    private String lastTrainTime;


}
