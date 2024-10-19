package com.TongYu.controller;

import com.TongYu.config.ApiResponse;
import com.TongYu.controller.Source.CourseRecordController;
import com.TongYu.dto.CourseAddRequest;
import com.TongYu.dto.CourseRequest;
import com.TongYu.dto.CourseResponse;
import com.TongYu.model.CourseRecord;
import com.TongYu.model.Student;
import com.TongYu.model.Trainer;
import com.TongYu.service.CourseRecordService;
import com.TongYu.service.LessonManagementService;
import com.TongYu.service.StudentService;
import com.TongYu.service.TrainerService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.beans.BeanUtils.copyProperties;

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
    public ApiResponse classReservation(@RequestBody CourseAddRequest courseAddRequest) {
        return ApiResponse.ok(lessonManagementService.classReservation(courseAddRequest));
    }

    /**
     * 根据学员ID查询课后反馈信息
     *
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
        return ApiResponse.ok(lessonManagementService.courseRecordList(courseRequest));
    }


}

