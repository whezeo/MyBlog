package com.xl.domain.service.impl;

import com.xl.domain.ResponseResult;
import com.xl.domain.entity.LoginUser;
import com.xl.domain.entity.User;
import com.xl.domain.service.BlogLoginService;
import com.xl.domain.utils.BeanCopyUtils;
import com.xl.domain.utils.JwtUtil;
import com.xl.domain.utils.RedisCache;
import com.xl.domain.vo.BlogUserLoginVo;
import com.xl.domain.vo.UserInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class BlogLoginServiceImpl implements BlogLoginService {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    RedisCache redisCache;
    @Override
    public ResponseResult login(User user) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(user.getUserName(),user.getPassword());
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        if(Objects.isNull(authenticate)){
            throw new RuntimeException("用户名或密码错误");
        }
        //获取userId生成token
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        Long id = loginUser.getUser().getId();
        String jwt = JwtUtil.createJWT(id.toString());
        //封装用户信息到redis
        redisCache.setCacheObject("blog_login_userId_" + id,loginUser);
        //返回vo给前端
        UserInfoVo userInfoVo = BeanCopyUtils.copyBean(loginUser, UserInfoVo.class);
        BlogUserLoginVo blogUserLoginVo = new BlogUserLoginVo(jwt, userInfoVo);
        return ResponseResult.okResult(blogUserLoginVo);
    }

    @Override
    public ResponseResult logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        Long id = loginUser.getUser().getId();
        //删除redis
        redisCache.deleteObject("blog_login_userId_"+ id);
        return ResponseResult.okResult();
    }
}
