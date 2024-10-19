package com.TongYu;

import com.TongYu.controller.WeComController;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@MapperScan(basePackages = {"com.TongYu.mapper"})
public class TongYuRunApplication {

  public static void main(String[] args) throws JSONException {
    SpringApplication.run(TongYuRunApplication.class, args);
//    new WeComController().getCorpAccessToken();
  }
}
