package com.TongYu.service;

import com.TongYu.model.Student;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author lanyiping
* @description 针对表【student(学员表)】的数据库操作Service
* @createDate 2024-10-11 00:50:56
*/
public interface StudentService extends IService<Student> {

    void getStudentInformation(String externalUserId);
}
