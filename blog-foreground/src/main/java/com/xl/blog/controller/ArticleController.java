package com.xl.blog.controller;

import com.xl.domain.ResponseResult;
import com.xl.domain.annotation.SystemLog;
import com.xl.domain.entity.Article;
import com.xl.domain.service.ArticleService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 文章表
 * @author xxll
 */
@RestController
@RequestMapping("/article")
@Api(tags="文章",description = "文章相关接口")
public class ArticleController {

    @Autowired
    ArticleService articleService;

//    @GetMapping
//    public List<Article> test(){
//        return articleService.list();
//    }

    @GetMapping("/hotArticleList")
    @SystemLog(businessName = "显示热门文章")
    public ResponseResult hotArticleList(){
        return articleService.getHotArticleList();
    }
    @SystemLog(businessName = "显示文章列表")
    @GetMapping("/articleList")
    public ResponseResult articleList(Integer pageNum,Integer pageSize,Long categoryId){
        return articleService.articleList(pageNum,pageSize,categoryId);
    }
    @GetMapping("/{id}")
    @SystemLog(businessName = "显示文章细节")
    public ResponseResult getArticleDetail(@PathVariable("id") Long id){
        return articleService.getArticleDetail(id);
    }
    @PutMapping("/updateViewCount/{id}")
    public ResponseResult updateViewCount(@PathVariable("id") Long id){
         return articleService.updateViewCount(id);
    }
}
