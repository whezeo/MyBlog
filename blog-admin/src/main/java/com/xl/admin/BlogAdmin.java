package com.xl.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author xxll
 */
@SpringBootApplication(scanBasePackages = "com.xl")
@MapperScan("com.xl.domain.mapper")
@EnableTransactionManagement
public class BlogAdmin {
    public static void main(String[] args) {
       SpringApplication.run(BlogAdmin.class,args);
    }
}
