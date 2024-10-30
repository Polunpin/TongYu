package com.TongYu.service.impl;

import com.TongYu.mapper.StudentMapper;
import com.TongYu.model.Student;
import com.TongYu.service.StudentService;
import com.TongYu.service.WeComService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
* @author lanyiping
* @description 针对表【student(学员表)】的数据库操作Service实现
* @createDate 2024-10-11 00:50:56
*/
@Slf4j
@Service
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student>
    implements StudentService{

    @Resource
    public WeComService weComService;

    @Override
    public void getStudentInformation(String externalUserId) {
        Student student = this.getOne(new QueryWrapper<Student>().eq("external_user_id", externalUserId));
        //第一次请求保存用户数据
        if (student == null) {
            weComService.registerStudent(externalUserId);
            student = this.getOne(new QueryWrapper<Student>().eq("external_user_id", externalUserId));
        }
        log.info("student:{}", student);
    }
}




