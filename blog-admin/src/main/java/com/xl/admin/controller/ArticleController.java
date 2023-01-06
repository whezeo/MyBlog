package com.xl.admin.controller;


import com.xl.domain.ResponseResult;
import com.xl.domain.dto.AddArticleDto;
import com.xl.domain.service.ArticleService;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * @author xxll
 */
@RestController
@RequestMapping("/content/article")
public class ArticleController {
    @Autowired
    ArticleService articleService;

    @PostMapping
    @PreAuthorize("@ps.hasPermissions('content:article:writer')")
    public ResponseResult add(@RequestBody AddArticleDto article){
        return articleService.add(article);
    }

    @GetMapping("list")
    public ResponseResult list(Integer pageNum, Integer pageSize,AddArticleDto articleDto){
        return articleService.list(pageNum,pageSize,articleDto);
    }
    @GetMapping("{id}")
    public ResponseResult getById(@PathVariable("id") Long id){
        return articleService.selectArticleById(id);
    }
    @PutMapping
    public ResponseResult update(@RequestBody AddArticleDto articleDto){
        articleService.updateArticle(articleDto);
        return ResponseResult.okResult();
    }
    @DeleteMapping("{id}")
    public ResponseResult delete(@PathVariable Long id){
        articleService.deleteById(id);
        return ResponseResult.okResult();
    }
}
