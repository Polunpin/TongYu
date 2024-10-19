package com.TongYu.service;

import com.TongYu.dto.CourseRequest;
import com.TongYu.model.CourseRecord;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author lanyiping
* @description 针对表【course_record(课时表)】的数据库操作Service
* @createDate 2024-10-11 01:03:15
*/
public interface CourseRecordService extends IService<CourseRecord> {

    /**
     * 课时列表查询
     * @param courseRequest 课时查询条件
     * @return 课时分页数据
     */
    Page<CourseRecord> listInfo(CourseRequest courseRequest);
}
