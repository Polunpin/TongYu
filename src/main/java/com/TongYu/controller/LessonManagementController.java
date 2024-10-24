package com.TongYu.controller;

import com.TongYu.config.ApiResponse;
import com.TongYu.dto.CourseAddRequest;
import com.TongYu.dto.CourseRequest;
import com.TongYu.model.CourseRecord;
import com.TongYu.service.LessonManagementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 课程管理业务逻辑处理
 *
 * @author TongYu
 * @version 1.0
 * @date 2024/10/14 01:21
 */
@Slf4j
@RestController()
@RequestMapping("/lessonManagement")
public class LessonManagementController {

    @Resource
    public LessonManagementService lessonManagementService;


    /**
     * 小程序首页-个人信息
     *
     * @return ApiResponse
     */
    @GetMapping("/personalInfo")
    public ApiResponse personalInfo(String unionId) {
        return ApiResponse.ok(lessonManagementService.personalInfo(unionId));
    }

    /**
     * 保存预约信息
     *
     * @param courseAddRequest 预约信息（学员个人信息+课程信息）
     * @return ApiResponse
     */
    @PostMapping("/classReservation")
    public ApiResponse reservation(@RequestBody CourseAddRequest courseAddRequest) {
        return ApiResponse.ok(lessonManagementService.reservation(courseAddRequest));
    }

    /**
     * 查询学员的课单信息
     * @param externalUserId 学员ID（微信标识）
     * @return ApiResponse
     */
    @GetMapping("/getReservationByExternalUserId")
    public ApiResponse getReservationByExternalUserId(String externalUserId) {
        return ApiResponse.ok(lessonManagementService.getReservationByExternalUserId(externalUserId));
    }

    /**
     * 添加预约信息（教练侧）
     *
     * @param courseAddRequest 预约信息（学员个人信息+课程信息）
     * @return ApiResponse
     */
    @PostMapping("/addCourseRecord")
    public ApiResponse addCourseRecord(@RequestBody CourseAddRequest courseAddRequest) {
        return ApiResponse.ok(lessonManagementService.addCourseRecord(courseAddRequest));
    }

    /**
     * 更新课单信息
     */
    @PostMapping("/updateCourseRecord")
    public ApiResponse updateCourseRecord(@RequestBody CourseRecord courseRecord) {
        return ApiResponse.ok(lessonManagementService.updateCourseRecord(courseRecord));
    }

    /**
     * 根据学员ID查询课后反馈信息
     * @return CourseResponse 课后反馈信息
     */
    @GetMapping("/feedback")
    public ApiResponse feedback(String studentId) {
        return ApiResponse.ok(lessonManagementService.feedback(studentId));
    }


    /**
     * 课单列表
     *
     * @param courseRequest 课程请求参数
     * @return ApiResponse
     */
    @PostMapping("/courseRecordList")
    public ApiResponse courseRecordList(@RequestBody CourseRequest courseRequest) {
        return lessonManagementService.courseRecordList(courseRequest);
    }

    /**
     * 学员课时统计
     *
     * @param workUserId 教练ID
     * @return ApiResponse
     */
    @GetMapping("/studentCourseCount")
    public ApiResponse studentCourseCount(String workUserId, String studentName) {
        return ApiResponse.ok(lessonManagementService.studentCourseCount(workUserId,studentName));
    }

}

