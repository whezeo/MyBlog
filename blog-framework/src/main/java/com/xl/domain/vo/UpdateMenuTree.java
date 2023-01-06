package com.xl.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMenuTree {
    private List<MenuTree> menus;
    private List<Long> checkedKeys;
}
