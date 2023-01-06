package com.xl.domain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xl.domain.ResponseResult;
import com.xl.domain.entity.Article;
import com.xl.domain.entity.Category;
import com.xl.domain.enums.SystemConstants;
import com.xl.domain.mapper.CategoryMapper;
import com.xl.domain.service.ArticleService;
import com.xl.domain.service.CategoryService;
import com.xl.domain.utils.BeanCopyUtils;
import com.xl.domain.vo.CategoryVO;
import com.xl.domain.vo.PageVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 分类表(Category)表服务实现类
 *
 * @author makejava
 * @since 2022-12-22 21:26:11
 */
@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {


    @Autowired
    ArticleService articleService;
    @Override
    public ResponseResult getCategoryList() {
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Article::getStatus, SystemConstants.ARTICLE_STATUS_NORMAL);
        //文章表中查询id
        List<Article> list = articleService.list(wrapper);
        //分类表中查询name
        Set<Long> categroyIds =  list.stream()
                .map(Article::getCategoryId)
                .collect(Collectors.toSet());
        List<Category> categories = super.listByIds(categroyIds);
        List<Category> categoryList = categories.stream()
                .filter(category -> category.getStatus().equals(SystemConstants.STATUS_NORMAL))
                .collect(Collectors.toList());
        //封装vo返回
        List<CategoryVO> categoryVOs = BeanCopyUtils.copyBeanList(categoryList, CategoryVO.class);
        return ResponseResult.okResult(categoryVOs);
    }

    @Override
    public ResponseResult listAllCategory() {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getStatus, SystemConstants.NORMAL);
        List<Category> list = list(wrapper);
        List<CategoryVO> categoryVOS = BeanCopyUtils.copyBeanList(list, CategoryVO.class);
        return ResponseResult.okResult(categoryVOS);
    }

    @Override
    public ResponseResult listCateGory(Integer pageNum, Integer pageSize, String name, String status) {
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.hasText(name),Category::getName,name);
        queryWrapper.eq(StringUtils.hasText(status),Category::getStatus,status);
        Page<Category> page = new Page<>(pageNum, pageSize);
        this.page(page,queryWrapper);
        List<Category> records = page.getRecords();
        List<CategoryVO> categoryVOS = BeanCopyUtils.copyBeanList(records, CategoryVO.class);
        PageVo pageVo = new PageVo(categoryVOS, page.getTotal());

        return ResponseResult.okResult(pageVo);
    }

    @Override
    public ResponseResult saveCategory(CategoryVO categoryVO) {
        Category category = BeanCopyUtils.copyBean(categoryVO, Category.class);
        this.save(category);

        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult selectById(Long id) {
        Category category = getById(id);
        CategoryVO categoryVO = BeanCopyUtils.copyBean(category, CategoryVO.class);
        return ResponseResult.okResult(categoryVO);
    }
}

