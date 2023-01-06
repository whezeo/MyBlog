package com.xl.domain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xl.domain.ResponseResult;
import com.xl.domain.entity.Menu;
import com.xl.domain.entity.RoleMenu;
import com.xl.domain.enums.AppHttpCodeEnum;
import com.xl.domain.enums.SystemConstants;
import com.xl.domain.exception.SystemException;
import com.xl.domain.mapper.MenuMapper;
import com.xl.domain.service.MenuService;
import com.xl.domain.service.RoleMenuService;
import com.xl.domain.utils.BeanCopyUtils;
import com.xl.domain.utils.RedisCache;
import com.xl.domain.utils.SecurityUtils;
import com.xl.domain.vo.MenuTree;
import com.xl.domain.vo.MenuVo;
import com.xl.domain.vo.UpdateMenuTree;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜单权限表(Menu)表服务实现类
 *
 * @author makejava
 * @since 2022-12-28 20:28:02
 */
@Service("menuService")
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {

    @Autowired
    RoleMenuService roleMenuService;
    @Override
    public List<String> selectPermsByUserId(Long userId) {
        //如果是管理员，返回所有的权限
        if(userId==1){
            LambdaQueryWrapper<Menu> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Menu::getStatus, SystemConstants.STATUS_NORMAL);
            List<Menu> list = list(queryWrapper);
            List<String> perms = list.stream().map(Menu::getPerms).collect(Collectors.toList());
            return perms;
        }
        //否则返回所具有的权限
        //多表查询
        return getBaseMapper().selectPermsByUserId(userId);
    }

    @Override
    public List<Menu> selectRouterMenuTreeByUserId(Long userId) {
        MenuMapper mapper = getBaseMapper();
        List<Menu> menus = null;
        //判断是否是管理员
        if(SecurityUtils.isAdmin()){
            //如果是 获取所有符合要求的Menu
           menus = mapper.selectAllRouters();
        }else{
            //否则  获取当前用户所具有的Menu
            menus = mapper.selectReutersByUserId(userId);
        }
        //构建tree
        //先找出第一层的菜单  然后去找他们的子菜单设置到children属性中
        List<Menu> menuTree = buildMenuTree(menus,0L);
        return menuTree;
    }

    @Override
    public ResponseResult list(String status, String menuName) {
        LambdaQueryWrapper<Menu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.hasText(status),Menu::getStatus,status);
        queryWrapper.like(StringUtils.hasText(menuName),Menu::getMenuName,menuName);
        queryWrapper.orderByAsc(Menu::getParentId).orderByAsc(Menu::getOrderNum);
        List<Menu> menuList = this.list(queryWrapper);
        List<MenuVo> menuVos = BeanCopyUtils.copyBeanList(menuList, MenuVo.class);

        return ResponseResult.okResult(menuVos);
    }

    @Override
    public ResponseResult selectById(Long id) {
        Menu menu = getById(id);
        MenuVo menuVo = BeanCopyUtils.copyBean(menu, MenuVo.class);
        return ResponseResult.okResult(menuVo);
    }

    @Override
    public ResponseResult updateMenu(Menu menu) {
        Long parentId = menu.getParentId();
        if(parentId.equals(menu.getId())){
            return ResponseResult.errorResult(AppHttpCodeEnum.UPDATE_MENU_ERROR);
        }
        this.updateById(menu);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult deleteMenu(Long menuId) {
        LambdaQueryWrapper<Menu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Menu::getParentId,menuId);
        List<Menu> list = this.list(queryWrapper);
        if(list.size()>0){
            return ResponseResult.errorResult(AppHttpCodeEnum.DELETE_MENU_ERROR);
        }
        removeById(menuId);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult treeselect() {
        List<Menu> menus = list();
        List<MenuTree> menuTrees = this.buildMenuTrees(menus,0L);
//        List<Menu> menuList = this.buildMenuTree(menus, 0);
//        List<MenuTree> menuTrees = menuList.stream().map(
//                menu -> {
//                    MenuTree menuTree = BeanCopyUtils.copyBean(menu, MenuTree.class);
//                    menuTree.setLabel(menu.getMenuName());
//                    List<MenuTree> children = menu.getChildren().stream().map(
//                            menu1 -> {
//                                MenuTree menuTree1 = BeanCopyUtils.copyBean(menu1, MenuTree.class);
//                                menuTree.setLabel(menu1.getMenuName());
//                                return menuTree1;
//                            }
//                    ).collect(Collectors.toList());
//                    menuTree.setChildren(children);

//                    return menuTree;
//                }
//        ).collect(Collectors.toList());

        return ResponseResult.okResult(menuTrees);
    }

    @Override
    public ResponseResult roleMenuTreeselect(Long id) {
        List<Menu> menus = list();
        List<MenuTree> menuTrees = this.buildMenuTrees(menus,0L);
        LambdaQueryWrapper<RoleMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RoleMenu::getRoleId,id);
        List<RoleMenu> roleMenus = roleMenuService.list(queryWrapper);
        List<Long> menuIds = roleMenus.stream()
                .map(RoleMenu::getMenuId)
                .collect(Collectors.toList());

        UpdateMenuTree updateMenuTree = new UpdateMenuTree(menuTrees, menuIds);
        return ResponseResult.okResult(updateMenuTree);
    }

    private List<MenuTree> buildMenuTrees(List<Menu> menus, Long parentId) {
        return menus.stream()
                .filter(menu -> menu.getParentId().equals(parentId))
                .map(menu ->  {
                    MenuTree menuTree = BeanCopyUtils.copyBean(menu, MenuTree.class);
                    menuTree.setLabel(menu.getMenuName());
                    menuTree.setChildren(this.getMenuTreeChildren(menus,menu));
                    return menuTree;
                }).collect(Collectors.toList());
    }

    private List<MenuTree> getMenuTreeChildren(List<Menu> menus, Menu menu) {
        return menus.stream()
                .filter(menu1 -> menu1.getParentId().equals(menu.getId()))
                .map(menu1 -> {
                    MenuTree menuTree = BeanCopyUtils.copyBean(menu1, MenuTree.class);
                    menuTree.setLabel(menu1.getMenuName());
                    menuTree.setChildren(this.getMenuTreeChildren(menus,menu1));
                    return menuTree;
                }).collect(Collectors.toList());
    }


    private List<Menu> buildMenuTree(List<Menu> menus, long parentId) {
       return menus.stream()
               .filter(menu -> menu.getParentId().equals(parentId))
               .map(menu->menu.setChildren(getChildren(menu,menus)))
               .collect(Collectors.toList());
    }

    private List<Menu> getChildren(Menu menu, List<Menu> menus) {
       return menus.stream()
               .filter(menu1 -> menu1.getParentId().equals(menu.getId()))
               .map(menu2 -> menu2.setChildren(getChildren(menu2,menus)))
               .collect(Collectors.toList());
    }

}

