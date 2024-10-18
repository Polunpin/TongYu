package com.TongYu.dto;

import com.TongYu.model.CourseRecord;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 学员预约课程请求参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CourseAddRequest extends CourseRecord {

    /**
     * 学员姓名
     */
    private String stuName;

    /**
     * 手机号
     */
    private String telephone;

    /**
     * 驾驶证图片
     */
    private String imageId;

}
