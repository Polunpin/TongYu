package com.TongYu.service.impl;

import com.TongYu.mapper.CourseRecordMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.TongYu.model.Student;
import com.TongYu.service.StudentService;
import com.TongYu.mapper.StudentMapper;
import org.springframework.stereotype.Service;

/**
* @author lanyiping
* @description 针对表【student(学员表)】的数据库操作Service实现
* @createDate 2024-10-11 00:50:56
*/
@Service
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student>
    implements StudentService{

}




