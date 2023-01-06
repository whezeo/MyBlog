package com.xl.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xl.domain.entity.Role;

import java.util.List;


/**
 * 角色信息表(Role)表数据库访问层
 *
 * @author makejava
 * @since 2022-12-28 20:28:42
 */
public interface RoleMapper extends BaseMapper<Role> {
    List<String> selectRolesByUserId(Long userId);

}

