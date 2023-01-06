package com.xl.blog.controller;

import com.xl.domain.ResponseResult;
import com.xl.domain.service.LinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/link")
public class LinkController {
    @Autowired
    LinkService linkService;

    @GetMapping("/getAllLink")
    public ResponseResult getALlLink(){
        return linkService.getAllLink();
    }
}
