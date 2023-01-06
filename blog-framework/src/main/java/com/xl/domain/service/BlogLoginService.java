package com.xl.domain.service;

import com.xl.domain.ResponseResult;
import com.xl.domain.entity.User;

public interface BlogLoginService {
    ResponseResult login(User user);

    ResponseResult logout();
}
