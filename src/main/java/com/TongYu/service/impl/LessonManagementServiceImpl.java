package com.TongYu.service.impl;

import com.TongYu.config.ApiResponse;
import com.TongYu.dto.*;
import com.TongYu.model.CourseRecord;
import com.TongYu.model.Student;
import com.TongYu.model.Trainer;
import com.TongYu.service.*;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            personalInfo.setIsAppointment(student.getUsed() > 0);
        } else {
            // 学员不存在时返回默认值
            personalInfo.setId(0L);
            personalInfo.setStuName("联系客服");
            personalInfo.setHeadImgUrl("");
            personalInfo.setGive(0);
            personalInfo.setLave(0);
            personalInfo.setUsed(0);
            personalInfo.setIsAppointment(Boolean.FALSE);
        }
        return personalInfo;
    }

    @Override
    public Boolean reservation(CourseAddRequest courseAddRequest) {
        Student student = new Student();
        // 使用正则表达式提取课程时长
        Matcher matcher = Pattern.compile("\\d+").matcher(courseAddRequest.getDuration());
        int duration = 0;
        if (matcher.find()) {
            duration = Integer.parseInt(matcher.group());
        }
        if (StringUtils.isNotBlank(courseAddRequest.getImageId())) {
            //体验课
            student.setId(courseAddRequest.getStudentId());
            student.setStuName(courseAddRequest.getStuName());
            student.setOpenId(courseAddRequest.getOpenId());
            student.setTelephone(courseAddRequest.getTelephone());
            student.setImage(courseAddRequest.getImageId());
            student.setUsed(duration);
        } else {
            //TODO 待完善-购买课时未同步到学生表
            student = studentService.getById(courseAddRequest.getStudentId());
            //正式课
            student.setLave(student.getLave() - duration);
            student.setUsed(student.getUsed() + duration);
            // 发送预约通知-教练(正式课)
            courseAddRequest.setScheduleId(weComService.createCalendar(courseAddRequest));
        }
        studentService.updateById(student);
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

            if (courseRecord.getTrainerId() == null) {
                courseResponse.setTrainerName("教练稍后报道 🫡");
            } else {
                courseResponse.setTrainerName(trainerService.getById(courseRecord.getTrainerId()).getTrainerName());
            }

            courseRecords.add(courseResponse);
        }
        return courseRecords;
    }

    private void setTrainerInfo(CourseRecord courseRecord, CourseResponse courseResponse) {
        if (courseRecord.getTrainerId() != null) {
            Trainer trainer = trainerService.getById(courseRecord.getTrainerId());
            String trainerName = StringUtils.isNotEmpty(trainer.getTrainerName()) ? "待分派" : trainer.getTrainerName();
            courseResponse.setTrainerName(trainerName);
            // 教练头像
            courseResponse.setTrainerAvatar("https://7072-prod-1gnzk6n75a8b6b8b-1327385705.tcb.qcloud.la/images/trainerImage.png?sign=dab6fafb43360b3154e9101fce9e1d83&t=1730302602");
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
        if (StringUtils.isBlank(courseRequest.getWorkUserId())) {
            return ApiResponse.error("教练ID不能为空");
        }
        log.info("当前查询教练id:{}", courseRequest.getWorkUserId());
        // 管理员角色
        Set<String> workUserIds = new HashSet<>(Arrays.asList("LanYiPing01", "tomorrow", "LanYiPing"));

        //判断是否为管理员角色,如果不是管理员角色，则只能查询自己的课单
        if (!workUserIds.contains(courseRequest.getWorkUserId())) {
            // 根据教练id(企微)查询教练信息
            QueryWrapper<Trainer> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("work_user_id", courseRequest.getWorkUserId());
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
        //分派教练-同步日历信息,并添加日历ID到courseRecord
        if (courseRecord.getState().equals("待上课")) {
            courseRecord.setScheduleId(weComService.createCalendar(courseRecord));
        }
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

        if (StringUtils.isNotBlank(studentName)) {
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
}




