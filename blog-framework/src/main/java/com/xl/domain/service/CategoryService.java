package com.xl.domain.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xl.domain.ResponseResult;
import com.xl.domain.entity.Category;
import com.xl.domain.vo.CategoryVO;


/**
 * 分类表(Category)表服务接口
 *
 * @author makejava
 * @since 2022-12-22 21:26:11
 */
public interface CategoryService extends IService<Category> {

    ResponseResult getCategoryList();

    ResponseResult listAllCategory();

    ResponseResult listCateGory(Integer pageNum, Integer pageSize, String name, String status);

    ResponseResult saveCategory(CategoryVO categoryVO);

    ResponseResult selectById(Long id);
}

