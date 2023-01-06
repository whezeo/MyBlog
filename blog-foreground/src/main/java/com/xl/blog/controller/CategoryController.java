package com.xl.blog.controller;

import com.xl.domain.ResponseResult;
import com.xl.domain.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    CategoryService categoryService;

    @GetMapping("/getCategoryList")
    public ResponseResult listCategory(){
       return  categoryService.getCategoryList();
    }
}
