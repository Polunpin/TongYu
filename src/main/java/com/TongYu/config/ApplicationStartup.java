package com.TongYu.config;

import com.TongYu.service.WeComService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class ApplicationStartup {

    @Resource
    public WeComService weComService;

    /**
     * 监听启动事件，执行初始化操作
     */
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        //配置全局token
        weComService.getCorpAccessToken();
    }
}
