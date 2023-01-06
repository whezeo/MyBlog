package com.xl.admin.controller;

import com.xl.domain.ResponseResult;
import com.xl.domain.dto.TagDto;
import com.xl.domain.service.TagService;
import com.xl.domain.vo.TagVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author xxll
 */
@RestController
@RequestMapping("/content/tag")
public class TagController {
    @Autowired
    private TagService tagService;
//
//    @GetMapping("/list")
//    public ResponseResult list(){
//        return ResponseResult.okResult(tagService.list());
//    }

    @GetMapping("/list")
    public ResponseResult list(Integer pageNum , Integer pageSize, TagDto tagdto){
        return tagService.listByName(pageNum,pageSize,tagdto);
    }
    @PostMapping
    public ResponseResult save(@RequestBody TagDto tagDto){
        tagService.save(tagDto);
        return ResponseResult.okResult();
    }
    @DeleteMapping("{id}")
    public ResponseResult delete(@PathVariable("id") Long id){
        tagService.delete(id);
        return ResponseResult.okResult();
    }

    @GetMapping("{id}")
    public ResponseResult getById(@PathVariable("id") Long id){
        return tagService.getTagById(id);
    }
    @PutMapping
    public ResponseResult updateTag(@RequestBody TagVo tagVo){
        tagService.save(tagVo);
        return ResponseResult.okResult();
    }
    @GetMapping("listAllTag")
    public ResponseResult listAllTag(){
        return tagService.listAllTag();
    }
}
