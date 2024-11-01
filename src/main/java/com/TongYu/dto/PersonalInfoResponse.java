package com.TongYu.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

/**
 * 首页-个人信息返回对象
 */
@Data
public class PersonalInfoResponse {

    /**
     * 学员主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 学员姓名
     */
    private String stuName;

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
     * 驾驶熟练度
     */
    private Integer levelNumber;

}
