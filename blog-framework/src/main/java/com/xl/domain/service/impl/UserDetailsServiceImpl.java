package com.xl.domain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xl.domain.entity.LoginUser;
import com.xl.domain.entity.User;
import com.xl.domain.enums.SystemConstants;
import com.xl.domain.mapper.MenuMapper;
import com.xl.domain.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserMapper userMapper;
    @Autowired
    MenuMapper menuMapper;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        /**
         * 根据id查询用户信息
         *   判断是否查到用户  如果没查到抛出异常
         */
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getUserName,username);
        User user = userMapper.selectOne(userLambdaQueryWrapper);
        if(Objects.isNull(user)){
            throw new RuntimeException("用户不存在");
        }
        //TODO 封装权限信息 后台系统封装权限
        if(user.getType().equals(SystemConstants.ADMAIN)){
            List<String> list = menuMapper.selectPermsByUserId(user.getId());
            return new LoginUser(user,list);
        }
        return  new LoginUser(user,null);
    }
}
