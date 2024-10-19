package com.TongYu.service.impl;

import com.TongYu.config.ApiResponse;
import com.TongYu.dto.PersonalInfoResponse;
import com.TongYu.mapper.StudentMapper;
import com.TongYu.model.CourseRecord;
import com.TongYu.model.Student;
import com.TongYu.service.CourseRecordService;
import com.TongYu.service.LessonManagementService;
import com.TongYu.service.StudentService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author lanyiping
 * @description 针对表【student(学员表)】的数据库操作Service实现
 * @createDate 2024-10-11 00:50:56
 */
@Service
public class LessonManagementServiceImpl implements LessonManagementService {

    @Resource
    public StudentService studentService;
    @Resource
    public CourseRecordService courseRecordService;

    @Override
    public PersonalInfoResponse personalInfo(String unionId) {
        QueryWrapper<Student> studentQw = new QueryWrapper<>();
        studentQw.eq("union_id", unionId);
        Student student = studentService.getOne(studentQw);
        // 学员的课时信息
        PersonalInfoResponse personalInfoResponse = new PersonalInfoResponse();
        if (student != null) {
            personalInfoResponse.setId(student.getId());
            personalInfoResponse.setStuName(student.getStuName());
            personalInfoResponse.setTotal(student.getTotal());
            personalInfoResponse.setGive(student.getGive());
            personalInfoResponse.setLave(student.getLave());
            personalInfoResponse.setUsed(student.getUsed());

            //根据学生id查询课程记录-已上课时
            QueryWrapper<CourseRecord> courseRecordQw = new QueryWrapper<>();
            // 学生id
            courseRecordQw.eq("student_id", student.getId());
            long isAppointment = courseRecordService.count(courseRecordQw);
            personalInfoResponse.setIsAppointment(isAppointment != 0);
            // 已完成的课时
            courseRecordQw.eq("state", "4");
            long count = courseRecordService.count(courseRecordQw);
            int levelNumber = 0;
            if (count > 0) {
                levelNumber = (int) Math.floorDiv(count, student.getTotal());
            }
            personalInfoResponse.setLevelNumber(levelNumber);

        } else {
            // 学员不存在时返回默认值
            personalInfoResponse.setId(0L);
            personalInfoResponse.setStuName("请先联系客服");
            personalInfoResponse.setTotal(0);
            personalInfoResponse.setGive(0);
            personalInfoResponse.setLave(0);
            personalInfoResponse.setUsed(0);
            personalInfoResponse.setLevelNumber(0);
            personalInfoResponse.setIsAppointment(Boolean.FALSE);
        }
        return personalInfoResponse;
    }
}




