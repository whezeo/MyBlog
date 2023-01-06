package com.xl.domain.service;

import com.xl.domain.ResponseResult;
import com.xl.domain.entity.User;

public interface LoginService {
    ResponseResult login(User user);

    void logout();
}
