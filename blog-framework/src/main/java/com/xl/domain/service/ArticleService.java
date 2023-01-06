package com.xl.domain.service;

import com.xl.domain.ResponseResult;
import com.xl.domain.dto.AddArticleDto;
import com.xl.domain.entity.Article;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author liveb
* @description 针对表【sg_article(文章表)】的数据库操作Service
* @createDate 2022-12-22 15:12:50
*/
public interface ArticleService extends IService<Article> {

    ResponseResult getHotArticleList();

    ResponseResult articleList(Integer pageNum, Integer pageSize, Long categoryId);

    ResponseResult getArticleDetail(Long id);

    ResponseResult updateViewCount(Long id);

    ResponseResult add(AddArticleDto article);

    ResponseResult list(Integer pageNum, Integer pageSize, AddArticleDto articleDto);

    /**
     * 根据id查询文章
     * @param id
     * @return
     */
    ResponseResult selectArticleById(Long id);

    /**
     * 更新文章表
     * @param articleDto
     */
    void updateArticle(AddArticleDto articleDto);

    void deleteById(Long id);
}
