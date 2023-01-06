package com.xl.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xxll
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryVO {

    private Long id;
    private String name;
    private String description;
    private String status;
}
