package com.xl.blog;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 *  blog
 * @author xxll
 */
@SpringBootApplication(scanBasePackages = "com.xl")
@MapperScan("com.xl.domain.mapper")
@EnableScheduling
@EnableSwagger2
public class MyBlogApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyBlogApplication.class,args);
    }
}