package com.xl.blog.controller;

import com.xl.domain.ResponseResult;
import com.xl.domain.entity.User;
import com.xl.domain.enums.AppHttpCodeEnum;
import com.xl.domain.exception.SystemException;
import com.xl.domain.service.BlogLoginService;
import io.jsonwebtoken.lang.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BlogLoginController {

    @Autowired
    private BlogLoginService blogLoginService;

    @PostMapping("/login")
    public ResponseResult login(@RequestBody User user){
        if(!Strings.hasText(user.getUserName())){
            //用户名不能为空
            throw new SystemException(AppHttpCodeEnum.REQUIRE_USERNAME);
        }
        return blogLoginService.login(user);
    }
    @PostMapping("/logout")
    public ResponseResult logout(){
        return blogLoginService.logout();
    }
}
