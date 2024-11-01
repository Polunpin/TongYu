package com.TongYu.dto;

import lombok.Data;

/**
 * 课时列表
 */
@Data
public class CourseRequest {

    /**
     * 学员ID
     */
    private String studentId;

    /**
     * 教练ID
     */
    private Long trainerId;

    /**
     * 课单状态
     */
    private String state;

    /**
     * 从第几页开始
     */
    private int startIndex;

    /**
     * 页面数量
     */
    private int pageSize;
}
