package com.TongYu.dto;

import lombok.Data;

/**
 * 学员列表
 */
@Data
public class StudentRequest {

    /**
     * 学员ID
     */
    private String studentId;
    /**
     * 学员名称
     */
    private String stuName;

    /**
     * 从第几页开始
     */
    private int startIndex;

    /**
     * 页面数量
     */
    private int pageSize;
}
