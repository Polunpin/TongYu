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
    public CourseRecordController courseRecordController;
    @Resource
    public CourseRecordService courseRecordService;
    @Resource
    public StudentService studentService;
    @Resource
    public TrainerService trainerService;
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
        Student student = new Student();
        student.setId(courseAddRequest.getStudentId());
        student.setStuName(courseAddRequest.getStuName());
        student.setTelephone(courseAddRequest.getTelephone());
        student.setImage(courseAddRequest.getImageId());
        studentService.updateById(student);

        // TODO 发送公众号：预约成功通知
        return ApiResponse.ok(courseRecordService.save(courseAddRequest));
    }

    /**
     * 根据学员ID查询课后反馈信息
     *
     * @return ApiResponse
     */
    @GetMapping("/feedback")
    public ApiResponse feedback(String studentId) {
        List<CourseResponse> courseRecords = new ArrayList<>();
        List<CourseRecord> courseRecordList =
                courseRecordService.list(new QueryWrapper<CourseRecord>().eq("student_id", studentId));
        for (CourseRecord courseRecord : courseRecordList) {
            CourseResponse courseResponse = new CourseResponse();
            copyProperties(courseRecord, courseResponse);
            if (courseRecord.getTrainerId() != null) {
                Trainer trainer = trainerService.getById(courseRecord.getTrainerId());
                courseResponse.setTrainerName(trainer.getTrainerName());
            }
            // 设置日期格式-预约日期
            SimpleDateFormat monthDayFormat = new SimpleDateFormat("MM月dd日");
            // 格式化日期-预约日期
            courseResponse.setAppointmentDate(monthDayFormat.format(courseRecord.getStartTime()));

            // 创建用于输出的格式-预约时间
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
            // 格式化开始时间和结束时间-预约时间
            String formattedStartTime = outputFormat.format(courseRecord.getStartTime());
            String formattedEndTime = outputFormat.format(courseRecord.getEndTime());
            // 拼接最终的格式-预约时间
            String appointmentTime = formattedStartTime + "-" + formattedEndTime.split(" ")[1];
            courseResponse.setAppointmentTime(appointmentTime);
            courseRecords.add(courseResponse);
        }
        return ApiResponse.ok(courseRecords);
    }


    /**
     * 课单列表
     */
    @PostMapping("/courseRecordList")
    public ApiResponse courseRecordList(@RequestBody CourseRequest courseRequest) {
        List<CourseResponse> courseResponses = new ArrayList<>();
        Page<CourseRecord> courseRecordPage = courseRecordController.listInfo(courseRequest);
        for (CourseRecord courseRecord : courseRecordPage.getRecords()) {
            CourseResponse courseResponse = new CourseResponse();
            copyProperties(courseRecord, courseResponse);
            // 计算时间差
            long timeDifference = courseRecord.getEndTime().getTime() - courseRecord.getStartTime().getTime();
            long diffInHours = timeDifference / (60 * 60 * 1000);

            if (courseRecord.getTrainerId() != null) {
                Trainer trainer = trainerService.getById(courseRecord.getTrainerId());
                Long lessonPrice = diffInHours * trainer.getTrainerPrice();
                courseResponse.setTrainerName(trainer.getTrainerName());
                //开始结束时间的 时间差*教练课时费用
                courseResponse.setPendingAmount(lessonPrice + "元" + "/" + diffInHours + "小时");
            }
            courseResponses.add(courseResponse);
        }
        return ApiResponse.ok(courseResponses);
    }
}

