package com.TongYu.mapper;

import com.TongYu.model.Trainer;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author lanyiping
* @description 针对表【trainer(教练表)】的数据库操作Mapper
* @createDate 2024-10-05 03:16:18
* @Entity com.TongYu.model.Trainer
*/
@Mapper
public interface TrainerMapper extends BaseMapper<Trainer> {

}




