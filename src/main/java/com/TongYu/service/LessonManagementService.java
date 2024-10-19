package com.TongYu.service;

import com.TongYu.dto.CourseAddRequest;
import com.TongYu.dto.CourseRequest;
import com.TongYu.dto.PersonalInfoResponse;
import com.TongYu.model.Student;

public interface LessonManagementService {


    /**
     * 小程序首页-个人信息
     *
     * @return ApiResponse
     */
    PersonalInfoResponse personalInfo(String unionId);

    /**
     * 保存预约信息
     *
     * @param courseAddRequest 预约信息（学员个人信息+课程信息）
     * @return Boolean 是否成功
     */
    Boolean classReservation(CourseAddRequest courseAddRequest);

    /**
     * 根据学员ID查询课后反馈信息
     *
     * @return CourseResponse 课后反馈信息
     */
    Object feedback(String studentId);

    /**
     * 课单列表
     *
     * @param courseRequest 课程请求参数
     * @return ApiResponse
     */
    Object courseRecordList(CourseRequest courseRequest);
}
