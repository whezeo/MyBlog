package com.xl.blog.timer;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


public class TestJob {

    @Scheduled(cron = "0/5 * * * * ?")
    public void testJob(){
        System.out.println("定时任务执行 ");
    }
}
