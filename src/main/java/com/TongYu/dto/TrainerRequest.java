package com.TongYu.dto;

import lombok.Data;

/**
 * 教练列表
 */
@Data
public class TrainerRequest {

    /**
     * 教练ID
     */
    private String trainerId;
    /**
     * 教练名称
     */
    private String trainerName;

    /**
     * 从第几页开始
     */
    private int startIndex;

    /**
     * 页面数量
     */
    private int pageSize;
}
