package com.TongYu.service;

import com.TongYu.dto.TrainerMobileResponse;
import com.TongYu.dto.TrainerRequest;
import com.TongYu.model.Student;
import com.TongYu.model.Trainer;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author lanyiping
* @description 针对表【trainer(教练表)】的数据库操作Service
* @createDate 2024-10-05 03:16:18
*/
public interface TrainerService extends IService<Trainer> {

    /**
     * 查询教练列表|移动端使用
     */
    List<TrainerMobileResponse> listInfoMobile(TrainerRequest trainerRequest);
}
