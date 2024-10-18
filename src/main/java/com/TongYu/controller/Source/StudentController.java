package com.TongYu.controller.Source;

import com.TongYu.config.ApiResponse;
import com.TongYu.dto.StudentRequest;
import com.TongYu.model.Student;
import com.TongYu.service.StudentService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@RestController()
@RequestMapping("/student")
public class StudentController {

    @Resource
    public StudentService studentService;


    @PostMapping("/listInfo")
    public ApiResponse listInfo(@RequestBody StudentRequest studentRequest) {
        //分页
        studentRequest.setStartIndex((studentRequest.getStartIndex() - 1) * studentRequest.getPageSize());
        Page<Student> page=new Page<>(studentRequest.getStartIndex(),studentRequest.getPageSize());
        QueryWrapper<Student> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("stu_name",studentRequest.getStuName());
        Page<Student> pages = studentService.page(page, queryWrapper);
        return ApiResponse.ok(pages);
    }

    @PostMapping("/save")
    public ApiResponse save(@RequestBody Student Student) {
        return ApiResponse.ok(studentService.save(Student));
    }

    @PostMapping("/update")
    public ApiResponse update(@RequestBody Student Student) {
        return ApiResponse.ok(studentService.updateById(Student));
    }

    @PostMapping("/updateBatchById")
    public ApiResponse updateBatchById(@RequestBody List<Student> Students) {
        return ApiResponse.ok(studentService.updateBatchById(Students));
    }
}
