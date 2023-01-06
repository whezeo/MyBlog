package com.xl.domain.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xl.domain.ResponseResult;
import com.xl.domain.entity.Menu;

import java.util.List;


/**
 * 菜单权限表(Menu)表服务接口
 *
 * @author makejava
 * @since 2022-12-28 20:28:02
 */
public interface MenuService extends IService<Menu> {

    List<String> selectPermsByUserId(Long userId);

    List<Menu> selectRouterMenuTreeByUserId(Long userId);

    ResponseResult list(String status, String menuName);

    ResponseResult selectById(Long id);

    ResponseResult updateMenu(Menu menu);

    ResponseResult deleteMenu(Long menuId);

    ResponseResult treeselect();

    ResponseResult roleMenuTreeselect(Long id);
}

