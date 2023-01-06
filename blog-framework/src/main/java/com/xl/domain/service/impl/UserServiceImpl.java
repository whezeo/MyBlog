package com.xl.domain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xl.domain.ResponseResult;
import com.xl.domain.entity.Role;
import com.xl.domain.entity.User;
import com.xl.domain.entity.UserRole;
import com.xl.domain.enums.AppHttpCodeEnum;
import com.xl.domain.exception.SystemException;
import com.xl.domain.mapper.UserMapper;
import com.xl.domain.service.UserRoleService;
import com.xl.domain.service.UserService;
import com.xl.domain.utils.BeanCopyUtils;
import com.xl.domain.utils.SecurityUtils;
import com.xl.domain.vo.PageVo;
import com.xl.domain.vo.UserInfoVo;
import com.xl.domain.vo.UserRoleVo;
import com.xl.domain.vo.UserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户表(User)表服务实现类
 *
 * @author makejava
 * @since 2022-12-24 20:57:43
 */
@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private RoleServiceImpl roleService;
    @Override
    public ResponseResult userInfo() {
        Long userId = SecurityUtils.getUserId();
        User user = this.getById(userId);
        UserInfoVo userInfoVo = BeanCopyUtils.copyBean(user, UserInfoVo.class);
        return ResponseResult.okResult(userInfoVo);
    }

    @Override
    public ResponseResult updateUserInfo(User user) {
        updateById(user);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult register(User user) {
        //判断为空
        if(!StringUtils.hasText(user.getUserName())){
            throw new SystemException(AppHttpCodeEnum.USER_NOT_NULL);
        }
        if(!StringUtils.hasText(user.getPassword())){
            throw new SystemException(AppHttpCodeEnum.USER_NOT_NULL);
        }
        if(!StringUtils.hasText(user.getEmail())){
            throw new SystemException(AppHttpCodeEnum.USER_NOT_NULL);
        }
        if(!StringUtils.hasText(user.getNickName())){
            throw new SystemException(AppHttpCodeEnum.USER_NOT_NULL);
        }
        //是否重复
        if(userNameExist(user.getUserName())){
            throw new SystemException(AppHttpCodeEnum.USERNAME_EXIST);
        }
        if(nickNameExist(user.getNickName())){
            throw new SystemException(AppHttpCodeEnum.NICKNAME_EXIST);
        }
        //密码加密
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        //存入数据库
        this.save(user);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult listUsers(Integer pageNum, Integer pageSize, String userName,String phonenumber, String status) {
        //条件
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.hasText(userName),User::getUserName,userName);
        queryWrapper.like(StringUtils.hasText(phonenumber),User::getPhonenumber,phonenumber);
        queryWrapper.eq(StringUtils.hasText(status),User::getStatus,status);
        Page<User> userPage = new Page<>(pageNum, pageSize);
        this.page(userPage,queryWrapper);
        List<User> users = userPage.getRecords();
        List<UserVo> userVos = BeanCopyUtils.copyBeanList(users, UserVo.class);
        PageVo pageVo = new PageVo(userVos, userPage.getTotal());
        return ResponseResult.okResult(pageVo);
    }

    @Override
    @Transactional
    public ResponseResult saveUser(User user) {
        //用户名不能为空 用户名不能重复
        String userName = user.getUserName();
        if(!StringUtils.hasText(userName)){
            return ResponseResult.errorResult(AppHttpCodeEnum.USER_NOT_NULL);
        }
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserName,userName);
        List<User> list1 = this.list(queryWrapper);
        if(list1.size()>0){
            return ResponseResult.errorResult(AppHttpCodeEnum.USERNAME_EXIST);
        }
        //邮箱手机号不能重复
        LambdaQueryWrapper<User> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(User::getEmail,user.getEmail());
        List<User> list2 = this.list(queryWrapper1);
        if(list2.size()>0){
            return ResponseResult.errorResult(AppHttpCodeEnum.EMAIL_EXIST);
        }

        LambdaQueryWrapper<User> queryWrapper2 = new LambdaQueryWrapper<>();
        queryWrapper2.eq(User::getPhonenumber,user.getPhonenumber());
        List<User> list3 = this.list(queryWrapper2);
        if(list3.size()>0){
            return ResponseResult.errorResult(AppHttpCodeEnum.PHONENUMBER_EXIST);
        }

        //密码加密
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        this.save(user);
        //保存role
        List<UserRole> userRoles = user.getRoleIds().stream().map(roleId -> new UserRole(user.getId(), roleId))
                .collect(Collectors.toList());
        userRoleService.saveBatch(userRoles);

        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult deleteUser(Long id) {
        this.removeById(id);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult selectById(Long id) {
        //查询用户
        User user = getById(id);
        UserVo userVo = BeanCopyUtils.copyBean(user, UserVo.class);
        //查询用户对应的角色
        LambdaQueryWrapper<UserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserRole::getUserId,id);
        List<UserRole> userRoles = userRoleService.list(queryWrapper);
        List<Long> roleIds = userRoles.stream().map(UserRole::getRoleId).collect(Collectors.toList());

        //查询所有角色
        List<Role> roles = roleService.list();
        //封装返回
        UserRoleVo userRoleVo = new UserRoleVo(roleIds, roles, userVo);
        return ResponseResult.okResult(userRoleVo);
    }

    @Override
    public ResponseResult updateUser(UserVo userVo) {
        User user = BeanCopyUtils.copyBean(userVo, User.class);
        this.updateById(user);
        //删除旧角色
        LambdaQueryWrapper<UserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserRole::getUserId,userVo.getId());
        userRoleService.remove(queryWrapper);
        //新增角色
        List<UserRole> userRoles = userVo.getRoleIds().stream().map(roleId -> new UserRole(userVo.getId(), roleId))
                .collect(Collectors.toList());
        userRoleService.saveBatch(userRoles);

        return ResponseResult.okResult();
    }

    private boolean nickNameExist(String nickName) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getNickName,nickName);
        int count = count(queryWrapper);
        return count != 0;
    }

    private boolean userNameExist(String userName) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserName,userName);
        int count = count(queryWrapper);
        return count != 0;
    }
}

