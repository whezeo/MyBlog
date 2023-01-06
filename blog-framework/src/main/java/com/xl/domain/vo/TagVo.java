package com.xl.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xxll
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
public class TagVo {
    private Long id;
    private String name;
    private String remark;
}
