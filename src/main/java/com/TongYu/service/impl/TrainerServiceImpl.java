package com.TongYu.service.impl;

import com.TongYu.dto.TrainerMobileResponse;
import com.TongYu.dto.TrainerRequest;
import com.TongYu.mapper.TrainerMapper;
import com.TongYu.model.Trainer;
import com.TongYu.service.TrainerService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.beans.BeanUtils.copyProperties;

/**
* @author lanyiping
* @description 针对表【trainer(教练表)】的数据库操作Service实现
* @createDate 2024-10-05 03:16:18
*/
@Service
public class TrainerServiceImpl extends ServiceImpl<TrainerMapper, Trainer>
    implements TrainerService {

    @Override
    public List<TrainerMobileResponse> listInfoMobile(TrainerRequest trainerRequest) {
        //查询条件
        QueryWrapper<Trainer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("trainer_status","在职");
        List<Trainer> list = this.list(queryWrapper);

        List<TrainerMobileResponse> trainerMobileList = new ArrayList<>();
        list.forEach(trainer -> {
            TrainerMobileResponse trainerMobile = new TrainerMobileResponse();
            copyProperties(trainer, trainerMobile);
            // 教练头像
            trainerMobile.setTrainerAvatar("https://7072-prod-1gnzk6n75a8b6b8b-1327385705.tcb.qcloud.la/images/trainerImage.png?sign=dab6fafb43360b3154e9101fce9e1d83&t=1730302602");
            trainerMobileList.add(trainerMobile);
        });
        return trainerMobileList;
    }
}
