package com.TongYu.controller.Source;

import com.TongYu.config.ApiResponse;
import com.TongYu.dto.CourseRequest;
import com.TongYu.model.CourseRecord;
import com.TongYu.service.CourseRecordService;
import com.TongYu.service.StudentService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@RestController()
@RequestMapping("/courseRecord")
public class CourseRecordController {

    @Resource
    public CourseRecordService courseRecordService;
    @Resource
    public StudentService studentService;


    @GetMapping("/listInfo")
    public Page<CourseRecord> listInfo(@RequestBody CourseRequest courseRequest) {
        //分页
        courseRequest.setStartIndex((courseRequest.getStartIndex() - 1) * courseRequest.getPageSize());
        Page<CourseRecord> page = new Page<>(courseRequest.getStartIndex(), courseRequest.getPageSize());
        QueryWrapper<CourseRecord> queryWrapper = new QueryWrapper<>();
        if (courseRequest.getState() != null) {
            queryWrapper.eq("state", courseRequest.getState());
        }
        //根据角色判断查询条件-教练
        if (courseRequest.getRole() == 2) {
            queryWrapper.eq("trainer_id", courseRequest.getTrainerId());
        }
        Page<CourseRecord> pages = courseRecordService.page(page, queryWrapper);
        return pages;
    }

    @GetMapping("/info")
    public ApiResponse info(@RequestBody CourseRequest courseRequest) {
        CourseRecord courseRecord = courseRecordService.getOne(new QueryWrapper<CourseRecord>().eq("id", courseRequest.getStudentId()));
        return ApiResponse.ok(courseRecord);
    }

    @PostMapping("/save")
    public ApiResponse save(@RequestBody CourseRecord courseRecord) {
        return ApiResponse.ok(courseRecordService.save(courseRecord));
    }


    @PostMapping("/update")
    public ApiResponse update(@RequestBody CourseRecord courseRecord) {
        return ApiResponse.ok(courseRecordService.updateById(courseRecord));
    }

    @PostMapping("/updateBatchById")
    public ApiResponse updateBatchById(@RequestBody List<CourseRecord> courseRecords) {
        return ApiResponse.ok(courseRecordService.updateBatchById(courseRecords));
    }
}
