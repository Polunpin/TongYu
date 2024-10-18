package com.TongYu.controller;

import com.TongYu.config.ApiResponse;
import com.TongYu.dto.CourseAddRequest;
import com.TongYu.model.Student;
import com.TongYu.service.CourseRecordService;
import com.TongYu.service.StudentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 预约业务逻辑处理
 * @author TongYu
 * @version 1.0
 * @date 2024/10/14 01:01
 */
@Slf4j
@RestController()
@RequestMapping("/reservation")
public class ReservationController {

}
