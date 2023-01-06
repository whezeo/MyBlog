package com.xl.domain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xl.domain.ResponseResult;
import com.xl.domain.entity.Role;
import com.xl.domain.entity.RoleMenu;
import com.xl.domain.enums.SystemConstants;
import com.xl.domain.mapper.RoleMapper;
import com.xl.domain.service.RoleMenuService;
import com.xl.domain.service.RoleService;
import com.xl.domain.utils.BeanCopyUtils;
import com.xl.domain.vo.PageVo;
import com.xl.domain.vo.RoleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色信息表(Role)表服务实现类
 *
 * @author makejava
 * @since 2022-12-28 20:28:42
 */
@Service("roleService")
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {
    @Autowired
    RoleMenuService roleMenuService;
    @Override
    public List<String> selectRolesByUserId(Long userId) {
        //管理员直接返回admin
        if(userId==1){
            List<String> roleKeys = new ArrayList<>();
            roleKeys.add("admin");
            return roleKeys;
        }
        //否则查询
        return getBaseMapper().selectRolesByUserId(userId);
    }

    @Override
    public ResponseResult list(Integer pageNum, Integer pageSize, String roleName, String status) {
        //查询条件
        LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.hasText(roleName),Role::getRoleName,roleName);
        queryWrapper.eq(StringUtils.hasText(status),Role::getStatus,status);
        queryWrapper.orderByAsc(Role::getRoleSort);
        //封装返回
        Page<Role> page = new Page<>();
        this.page(page,queryWrapper);
        List<Role> roles = page.getRecords();
        List<RoleVo> roleVos = BeanCopyUtils.copyBeanList(roles, RoleVo.class);
        PageVo pageVo = new PageVo(roleVos, page.getTotal());
        return ResponseResult.okResult(pageVo);
    }

    @Override
    public ResponseResult changeStatus(Long roleId, String status) {
        Role role = new Role();
        role.setId(roleId);
        role.setStatus(status);
        this.updateById(role);
        return ResponseResult.okResult();
    }

    @Override
    @Transactional
    public ResponseResult saveRole(RoleVo roleVo) {
        Role role = BeanCopyUtils.copyBean(roleVo, Role.class);
        this.save(role);
        List<RoleMenu> roleMenus = roleVo.getMenuIds().stream()
                .map(menuId -> new RoleMenu( role.getId(),menuId))
                .collect(Collectors.toList());
        roleMenuService.saveBatch(roleMenus);

        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult selectRoleById(Long id) {
        Role role = getById(id);
        RoleVo roleVo = BeanCopyUtils.copyBean(role, RoleVo.class);
        return ResponseResult.okResult(roleVo);
    }

    @Override
    @Transactional
    public ResponseResult updateRole(RoleVo roleVo) {
        //更新role
        Role role = BeanCopyUtils.copyBean(roleVo, Role.class);
        this.updateById(role);
        //删除原
        LambdaQueryWrapper<RoleMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RoleMenu::getRoleId,roleVo.getId());
        roleMenuService.remove(queryWrapper);
        //新增
        List<RoleMenu> roleMenus = roleVo.getMenuIds().stream().map(menuId -> new RoleMenu(roleVo.getId(), menuId)).collect(Collectors.toList());
        roleMenuService.saveBatch(roleMenus);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult listAllRole() {
        LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Role::getStatus, SystemConstants.STATUS_NORMAL);
        List<Role> roles = this.list(queryWrapper);

        return ResponseResult.okResult(roles);
    }
}

