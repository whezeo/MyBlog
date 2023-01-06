package com.xl.admin.controller;


import com.xl.domain.ResponseResult;
import com.xl.domain.dto.RoleDto;
import com.xl.domain.service.RoleService;
import com.xl.domain.vo.RoleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author xxll
 */
@RestController
@RequestMapping("/system/role")
public class RoleController {
    @Autowired
    RoleService roleService;
    @GetMapping("list")
    public ResponseResult list(Integer pageNum,Integer pageSize,String roleName,String status){
        return roleService.list(pageNum,pageSize,roleName,status);
    }

    @PutMapping("/changeStatus")
    public ResponseResult changeStatus(@RequestBody RoleDto roleDto){
        return roleService.changeStatus(roleDto.getRoleId(),roleDto.getStatus());
    }

    @PostMapping
    public ResponseResult save(@RequestBody RoleVo roleVo){
        return roleService.saveRole(roleVo);
    }
    @GetMapping("{id}")
    public ResponseResult getById(@PathVariable("id") Long id){
        return roleService.selectRoleById(id);
    }
    @PutMapping
    public ResponseResult update(@RequestBody RoleVo roleVo){
        return roleService.updateRole(roleVo);
    }
    @DeleteMapping("{id}")
    public ResponseResult delete(@PathVariable("id") Long id){
        roleService.removeById(id);
        return ResponseResult.okResult();
    }
    @GetMapping("listAllRole")
    public ResponseResult listAllRole(){
        return roleService.listAllRole();
    }
}
