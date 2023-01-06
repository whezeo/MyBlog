package com.xl.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xl.domain.entity.Category;
import org.apache.ibatis.annotations.Mapper;


/**
 * 分类表(Category)表数据库访问层
 *
 * @author makejava
 * @since 2022-12-22 21:26:11
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {

}

