package com.xl.domain.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xl.domain.ResponseResult;
import com.xl.domain.entity.Role;
import com.xl.domain.vo.RoleVo;

import java.util.List;


/**
 * 角色信息表(Role)表服务接口
 *
 * @author makejava
 * @since 2022-12-28 20:28:42
 */
public interface RoleService extends IService<Role> {

    List<String> selectRolesByUserId(Long userId);

    ResponseResult list(Integer pageNum, Integer pageSize, String roleName, String status);

    ResponseResult changeStatus(Long roleId, String status);

    ResponseResult saveRole(RoleVo roleVo);

    ResponseResult selectRoleById(Long id);

    ResponseResult updateRole(RoleVo roleVo);

    ResponseResult listAllRole();
}

