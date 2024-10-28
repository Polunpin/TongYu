package com.TongYu.service.impl;

import com.TongYu.config.ApiResponse;
import com.TongYu.dto.*;
import com.TongYu.model.CourseRecord;
import com.TongYu.model.Student;
import com.TongYu.model.Trainer;
import com.TongYu.service.*;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.springframework.beans.BeanUtils.copyProperties;

/**
 * @author lanyiping
 * @description 课单管理
 * @createDate 2024-10-11 00:50:56
 */
@Slf4j
@Service
public class LessonManagementServiceImpl implements LessonManagementService {

    @Resource
    public StudentService studentService;
    @Resource
    public CourseRecordService courseRecordService;
    @Resource
    public TrainerService trainerService;
    @Resource
    public WeComService weComService;

    @Override
    public PersonalInfoResponse personalInfo(String unionId) {
        Student student = studentService.getOne(new QueryWrapper<Student>().eq("union_id", unionId));
        // 学员的课时信息
        PersonalInfoResponse personalInfo = new PersonalInfoResponse();
        if (student != null) {
            log.info("当前查询学生Info:{}", student);
            copyProperties(student, personalInfo);
            personalInfo.setStuName(student.getStuName() == null ? "待约课" : student.getStuName());
            JSONObject wxCustomerDetails = weComService.getWxCustomerDetails(student.getExternalUserId());
            personalInfo.setHeadImgUrl(wxCustomerDetails.getJSONObject("external_contact").getString("avatar"));
            personalInfo.setIsAppointment(student.getUsed() > 0);
            //TODO 待完善-熟练度由教练完成
            personalInfo.setLevelNumber(0);
        } else {
            // 学员不存在时返回默认值
            personalInfo.setId(0L);
            personalInfo.setStuName("请联系客服");
            personalInfo.setHeadImgUrl("");
            personalInfo.setTotal(0);
            personalInfo.setGive(0);
            personalInfo.setLave(0);
            personalInfo.setUsed(0);
            personalInfo.setLevelNumber(0);
            personalInfo.setIsAppointment(Boolean.FALSE);
        }
        return personalInfo;
    }

    @Override
    public Boolean reservation(CourseAddRequest courseAddRequest) {
        Student student = new Student();
        if (courseAddRequest.getImageId() == null || "".equals(courseAddRequest.getImageId())) {
            //体验课
            student.setStuName(courseAddRequest.getStuName());
            student.setOpenId(courseAddRequest.getOpenId());
            student.setTelephone(courseAddRequest.getTelephone());
            student.setImage(courseAddRequest.getImageId());
            student.setUsed(courseAddRequest.getDuration());
        } else {
            //TODO 待完善-购买课时未同步到学生表
            student =studentService.getById(courseAddRequest.getStudentId());
            //正式课
            student.setLave(student.getLave() - courseAddRequest.getDuration());
            student.setUsed(student.getUsed() + courseAddRequest.getDuration());
        }
        studentService.updateById(student);
        // TODO 待完善-发送服务通知：预约成功通知
        return courseRecordService.save(courseAddRequest);
    }

    @Override
    public Object feedback(String studentId) {
        List<CourseResponse> courseRecords = new ArrayList<>();
        List<CourseRecord> courseRecordList = courseRecordService.list(new QueryWrapper<CourseRecord>().eq("student_id", studentId));

        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        for (CourseRecord courseRecord : courseRecordList) {
            CourseResponse courseResponse = new CourseResponse();
            copyProperties(courseRecord, courseResponse);
            // 设置教练信息
            setTrainerInfo(courseRecord, courseResponse);
            // 设置预约日期
            LocalDateTime startDateTime = convertToLocalDateTime(courseRecord.getStartTime());
            courseResponse.setAppointmentDateByMonth(startDateTime.getMonthValue());
            courseResponse.setAppointmentDateByDay(startDateTime.getDayOfMonth());
            // 设置预约时间
            String appointmentTime = formatAppointmentTime(courseRecord, outputFormat);
            courseResponse.setAppointmentTime(appointmentTime);
            courseRecords.add(courseResponse);
        }
        return courseRecords;
    }

    private void setTrainerInfo(CourseRecord courseRecord, CourseResponse courseResponse) {
        if (courseRecord.getTrainerId() != null) {
            Trainer trainer = trainerService.getById(courseRecord.getTrainerId());
            String trainerName = (trainer != null && !trainer.getTrainerName().isEmpty()) ? trainer.getTrainerName() : "待分派教练";
            courseResponse.setTrainerName(trainerName);
            // 教练头像 TODO 待完善
            courseResponse.setTrainerAvatar("http://wx.qlogo.cn/mmhead/Q3auHgzwzM4Dib3uiaibRsBe2LOiavArtYIIyQoZ0b8cDpNdM53b9f3VIw/0");
        }
    }

    private LocalDateTime convertToLocalDateTime(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    private String formatAppointmentTime(CourseRecord courseRecord, SimpleDateFormat outputFormat) {
        String formattedStartTime = outputFormat.format(courseRecord.getStartTime());
        String formattedEndTime = outputFormat.format(courseRecord.getEndTime());
        return formattedStartTime + "-" + formattedEndTime.split(" ")[1];
    }

    @Override
    public ApiResponse courseRecordList(CourseRequest courseRequest) {
        List<CourseResponse> courseResponses = new ArrayList<>();
        if (courseRequest.getWorkUserId().isEmpty()) {
            return ApiResponse.error("教练ID不能为空");
        }
        log.info("当前查询教练id:{}", courseRequest.getWorkUserId());
        // 管理员角色
        Set<String> workUserIds = new HashSet<>(Arrays.asList("LanYiPing01", "tomorrow", "LanYiPing"));

        // 根据教练id(企微)查询教练信息
        QueryWrapper<Trainer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("work_user_id", courseRequest.getWorkUserId());
        //判断是否为管理员角色,如果不是管理员角色，则只能查询自己的课单
        if (!workUserIds.contains(courseRequest.getWorkUserId())) {
            courseRequest.setTrainerId(String.valueOf(trainerService.getOne(queryWrapper).getId()));
        }
        Page<CourseRecord> courseRecordPage = courseRecordService.listInfo(courseRequest);
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
            if (courseRecord.getStudentId() != null) {
                log.info("当前查询学生id:{}", courseRecord.getStudentId());
                Student student = studentService.getById(courseRecord.getStudentId());
                courseResponse.setExternalUserId(student.getExternalUserId());
                JSONObject wxCustomerDetails = weComService.getWxCustomerDetails(student.getExternalUserId());
                courseResponse.setHeadImgUrl(wxCustomerDetails.getJSONObject("external_contact").getString("avatar"));
                courseResponse.setStudentName(student.getStuName());
            }
            courseResponses.add(courseResponse);
        }
        return ApiResponse.ok((int) courseRecordPage.getTotal(), courseResponses);
    }

    @Override
    public Object updateCourseRecord(CourseRecord courseRecord) {
        //TODO 待完善-预约上课成功通知（上课预约信息、教练信息、学员信息）
        //TODO 待完善-教练-反馈信息（学员上课反馈、教练评价）
        //TODO 待完善-学员-上课信息
        return courseRecordService.updateById(courseRecord);
    }

    @Override
    public List<StudentCourseResponse> studentCourseCount(String workUserId, String studentName) {

        // 根据教练id+学员姓名 查询学生列表
        QueryWrapper<Student> studentQw = new QueryWrapper<>();

        // 管理员角色
        Set<String> workUserIds = new HashSet<>(Arrays.asList("LanYiPing01", "tomorrow", "LanYiPing"));
        //判断是否为管理员角色,如果不是管理员角色，则只能查询自己的课单
        if (!workUserIds.contains(workUserId)) {
            //根据当前教练唯一标识（企微）查询教练id
            Long trainerId = trainerService.getOne(new QueryWrapper<Trainer>().eq("work_user_id", workUserId)).getId();
            studentQw.eq("trainer_id", trainerId);
        }

        if (studentName != null && !studentName.isEmpty()) {
            studentQw.like("stu_name", studentName);
        }

        List<Student> studentList = studentService.list(studentQw);
        List<StudentCourseResponse> studentCourses = new ArrayList<>();
        // 根据学生id查询课程记录
        studentList.forEach(student -> {
            QueryWrapper<CourseRecord> courseRecordQw = new QueryWrapper<>();
            courseRecordQw.eq("student_id", student.getId());
            courseRecordQw.orderByDesc("end_time").last("limit 1");
            CourseRecord courseRecord = courseRecordService.getOne(courseRecordQw);
            //上次练车时间+相差天数
            if (courseRecord != null) {
                StudentCourseResponse studentCourseResponse = new StudentCourseResponse();
                copyProperties(student, studentCourseResponse);
                // 将 courseRecord.getEndTime() 从 Date 转换为 LocalDateTime
                LocalDateTime endTime = courseRecord.getEndTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                // 计算相差的天数(最近一次练车时间-当前时间)
                long daysBetween = ChronoUnit.DAYS.between(endTime, LocalDateTime.now());
                studentCourseResponse.setLastTrainTime(courseRecord.getEndTime());
                studentCourseResponse.setLastTrainDays(daysBetween + "天前");
                studentCourses.add(studentCourseResponse);
            }
        });
        return studentCourses;
    }

    @Override
    public Object addCourseRecord(CourseAddRequest courseAddRequest) {
        return courseRecordService.save(courseAddRequest);
    }

    @Override
    public Object getReservationByStudentId(String studentId) {
        CourseAddRequest courseAddRequest = new CourseAddRequest();
        //课时信息
        QueryWrapper<CourseRecord> courseRecordQw = new QueryWrapper<>();
        courseRecordQw.eq("student_id", studentId);
        courseRecordQw.orderByDesc("end_time").last("limit 1");
        CourseRecord courseRecord = courseRecordService.getOne(courseRecordQw);
        copyProperties(courseRecord, courseAddRequest);
        //学员信息
        Student student = studentService.getById(studentId);
        courseAddRequest.setStuName(student.getStuName());
        courseAddRequest.setTelephone(student.getTelephone());
        courseAddRequest.setTrainerName(trainerService.getById(student.getTrainerId()).getTrainerName());

        return courseAddRequest;
    }
    //TODO 待完善-回调 支付成功通知-购买课程通知
}




