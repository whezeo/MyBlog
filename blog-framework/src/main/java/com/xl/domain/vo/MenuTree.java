package com.xl.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author liveb
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuTree {

    private Long id;
    private String label;
    private Long parentId;
    private List<MenuTree> children;
}
