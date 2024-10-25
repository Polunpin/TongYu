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
     * 学员姓名
     */
    private String studentName;

    /**
     * 学员头像
     */
    private String headImgUrl;

    /**
     * 教练姓名
     */
    private String trainerName;

    /**
     * 教练头像
     */
    private String trainerAvatar;

    /**
     * 预约日期-月份
     */
    private int appointmentDateByMonth;

    /**
     * 预约日期-日期
     */
    private int appointmentDateByDay;

    /**
     * 预约时间
     */
    private String appointmentTime;

    /**
     * 待结算金额
     */
    private String pendingAmount;

    /**
     * 外部联系人的userid
     */
    private String externalUserId;

}
