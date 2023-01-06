package com.xl.domain.service;




import com.xl.domain.utils.SecurityUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author xxll
 */
@Service("ps")
public class PermissionService {
    /**
     * 判断当前用户是否具有permission
     * @param permission 要判断的权限
     * @return
     */
    public boolean hasPermissions(String permission){
        if(SecurityUtils.isAdmin()){
            return true;
        }
        List<String> permissions = SecurityUtils.getLoginUser().getPermissions();
        return permissions.contains(permission);
    }
}
