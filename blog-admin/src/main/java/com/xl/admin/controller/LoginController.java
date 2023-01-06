package com.xl.admin.controller;

import com.xl.domain.ResponseResult;
import com.xl.domain.entity.Menu;
import com.xl.domain.entity.User;
import com.xl.domain.enums.AppHttpCodeEnum;
import com.xl.domain.exception.SystemException;
import com.xl.domain.service.LoginService;
import com.xl.domain.service.MenuService;
import com.xl.domain.service.RoleService;
import com.xl.domain.utils.BeanCopyUtils;
import com.xl.domain.utils.SecurityUtils;
import com.xl.domain.vo.AdminUserInfoVo;
import com.xl.domain.vo.RoutersVo;
import com.xl.domain.vo.UserInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author xxll
 */
@RestController
public class LoginController {
    @Autowired
    private LoginService loginService;
    @Autowired
    private MenuService menuService;
    @Autowired
    private RoleService roleService;

    @PostMapping("/user/login")
    public ResponseResult login(@RequestBody User user){
        if(!StringUtils.hasText(user.getUserName())){
            //提示 必须要传用户名
            throw new SystemException(AppHttpCodeEnum.REQUIRE_USERNAME);
        }
        return loginService.login(user);
    }
    @GetMapping("/getInfo")
    public ResponseResult<AdminUserInfoVo> getInfo(){
        //获取当前登录的用户
        Long userId = SecurityUtils.getUserId();
        //根据用户id查询权限信息
        List<String> perms = menuService.selectPermsByUserId(userId);
        //根据用户id查询角色信息
        List<String> roles = roleService.selectRolesByUserId(userId);
        //获取用户信息
        User user = SecurityUtils.getLoginUser().getUser();
        UserInfoVo userInfoVo = BeanCopyUtils.copyBean(user, UserInfoVo.class);
        //封装数据返回
        return ResponseResult.okResult(new AdminUserInfoVo(perms,roles,userInfoVo));
    }
    @GetMapping("/getRouters")
    public ResponseResult<Menu> getResources(){
        Long userId = SecurityUtils.getUserId();
        //查询menu 结果是tree的形式
        List<Menu> menus = menuService.selectRouterMenuTreeByUserId(userId);
        //封装数据返回
        return ResponseResult.okResult(new RoutersVo(menus));
    }
    @PostMapping("/user/logout")
    public ResponseResult logout(){
        loginService.logout();
        return ResponseResult.okResult();
    }
}