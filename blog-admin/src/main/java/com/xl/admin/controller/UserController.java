package com.xl.admin.controller;


import com.xl.domain.ResponseResult;
import com.xl.domain.entity.User;
import com.xl.domain.service.UserService;
import com.xl.domain.utils.BeanCopyUtils;
import com.xl.domain.vo.UserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author xxll
 */
@RestController
@RequestMapping("/system/user")
public class UserController {

    @Autowired
    UserService userService;
    @GetMapping("list")
    public ResponseResult list(Integer pageNum,Integer pageSize,String userName,String phonenumber,String status){
        return userService.listUsers(pageNum,pageSize,userName,phonenumber,status);
    }
    @PostMapping
    public ResponseResult save(@RequestBody User user){
       return  userService.saveUser(user);
    }
    @DeleteMapping("{id}")
    public ResponseResult delete(@PathVariable("id") Long id){
        return userService.deleteUser(id);
    }
    @GetMapping("/{id}")
    public ResponseResult getById(@PathVariable("id") Long id ){
        return userService.selectById(id);
    }
    @PutMapping()
    public ResponseResult update(@RequestBody UserVo userVo){
        return userService.updateUser(userVo);
    }
}
