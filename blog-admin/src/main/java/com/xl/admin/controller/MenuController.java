package com.xl.admin.controller;


import com.xl.domain.ResponseResult;
import com.xl.domain.entity.Menu;
import com.xl.domain.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author xxll
 */
@RestController
@RequestMapping("/system/menu")
public class MenuController {
    @Autowired
    MenuService menuService;

    @GetMapping("list")
    public ResponseResult list (String status ,String menuName) {
        return menuService.list(status,menuName);
    }

    @GetMapping("{id}")
    public ResponseResult getById(@PathVariable("id") Long id){
        return menuService.selectById(id);
    }

    @PutMapping
    public ResponseResult update(@RequestBody Menu menu){
        return menuService.updateMenu(menu);
    }
    @DeleteMapping("{menuId}")
    public ResponseResult delete(@PathVariable Long menuId){
        return menuService.deleteMenu(menuId);
    }
    @PostMapping
    public ResponseResult save(@RequestBody Menu menu){
        menuService.save(menu);
        return ResponseResult.okResult();
    }
    @GetMapping("/treeselect")
    public ResponseResult treeselect(){
        return menuService.treeselect();
    }
    @GetMapping("/roleMenuTreeselect/{id}")
    public ResponseResult roleMenuTreeselect(@PathVariable Long id){
        return  menuService.roleMenuTreeselect(id);
    }
}
