package com.xl.domain.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xl.domain.ResponseResult;
import com.xl.domain.entity.User;
import com.xl.domain.vo.UserVo;


/**
 * 用户表(User)表服务接口
 *
 * @author makejava
 * @since 2022-12-24 20:57:43
 */
public interface UserService extends IService<User> {

    ResponseResult userInfo();

    ResponseResult updateUserInfo(User user);

    ResponseResult register(User user);

    ResponseResult listUsers(Integer pageNum, Integer pageSize, String userName,String phonenumber, String status);

    ResponseResult saveUser(User user);

    ResponseResult deleteUser(Long id);

    ResponseResult selectById(Long id);

    ResponseResult updateUser(UserVo userVo);
}

