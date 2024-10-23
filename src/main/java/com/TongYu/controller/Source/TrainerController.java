package com.TongYu.controller.Source;

import com.TongYu.config.ApiResponse;
import com.TongYu.dto.TrainerRequest;
import com.TongYu.model.Trainer;
import com.TongYu.service.TrainerService;
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
@RequestMapping("/trainer")
public class TrainerController {

    @Resource
    public TrainerService trainerService;

    /**
     * 查询教练列表|PC使用
     */
    @PostMapping("/listInfo")
    public ApiResponse listInfo(@RequestBody TrainerRequest trainerRequest) {
        //分页
        trainerRequest.setStartIndex((trainerRequest.getStartIndex() - 1) * trainerRequest.getPageSize());
        Page<Trainer> page=new Page<>(trainerRequest.getStartIndex(),trainerRequest.getPageSize());
        //查询条件
        QueryWrapper<Trainer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("trainer_name",trainerRequest.getTrainerName());
        queryWrapper.eq("trainerStatus",1);
        Page<Trainer> pages = trainerService.page(page, queryWrapper);
        return ApiResponse.ok(pages);
    }

    /**
     * 查询教练列表|移动端使用
     */
    @PostMapping("/listInfoMobile")
    public ApiResponse listInfoMobile(@RequestBody TrainerRequest trainerRequest) {
        return ApiResponse.ok(trainerService.listInfoMobile(trainerRequest));
    }

    @PostMapping("/save")
    public ApiResponse save(@RequestBody Trainer trainer) {
        return ApiResponse.ok(trainerService.save(trainer));
    }

    @PostMapping("/update")
    public ApiResponse update(@RequestBody Trainer trainer) {
        return ApiResponse.ok(trainerService.updateById(trainer));
    }

    @PostMapping("/updateBatchById")
    public ApiResponse updateBatchById(@RequestBody List<Trainer> trainers) {
        return ApiResponse.ok(trainerService.updateBatchById(trainers));
    }
}
