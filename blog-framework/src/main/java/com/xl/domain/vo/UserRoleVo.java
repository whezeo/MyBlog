package com.xl.domain.vo;

import com.xl.domain.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author xxll
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserRoleVo {

    private List<Long> roleIds;
    private List<Role> roles;
    private UserVo user;
}
