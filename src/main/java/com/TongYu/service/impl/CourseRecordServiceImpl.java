package com.TongYu.service.impl;

import com.TongYu.mapper.CourseRecordMapper;
import com.TongYu.model.CourseRecord;
import com.TongYu.service.CourseRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author lanyiping
 * @description 针对表【course_record(课时表)】的数据库操作Service实现
 * @createDate 2024-10-11 01:03:15
 */
@Service
public class CourseRecordServiceImpl extends ServiceImpl<CourseRecordMapper, CourseRecord>
        implements CourseRecordService {
}




