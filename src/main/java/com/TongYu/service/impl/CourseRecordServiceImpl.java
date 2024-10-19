package com.TongYu.service.impl;

import com.TongYu.dto.CourseRequest;
import com.TongYu.mapper.CourseRecordMapper;
import com.TongYu.model.CourseRecord;
import com.TongYu.service.CourseRecordService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author lanyiping
 * @description 针对表【course_record(课时表)】的数据库操作Service实现
 * @createDate 2024-10-11 01:03:15
 */
@Service
public class CourseRecordServiceImpl extends ServiceImpl<CourseRecordMapper, CourseRecord>
        implements CourseRecordService {

    @Override
    public Page<CourseRecord> listInfo(CourseRequest courseRequest) {
        //分页
        courseRequest.setStartIndex((courseRequest.getStartIndex() - 1) * courseRequest.getPageSize());
        Page<CourseRecord> page = new Page<>(courseRequest.getStartIndex(), courseRequest.getPageSize());
        //查询条件 - 状态 - 教练
        QueryWrapper<CourseRecord> queryWrapper = new QueryWrapper<>();
        //状态
        if (!courseRequest.getState().isEmpty()) {
            queryWrapper.eq("state", courseRequest.getState());
        }
        //教练
        if (!courseRequest.getTrainerId().isEmpty()) {
            queryWrapper.eq("trainer_id", courseRequest.getTrainerId());
        }
        return this.page(page, queryWrapper);
    }
}




