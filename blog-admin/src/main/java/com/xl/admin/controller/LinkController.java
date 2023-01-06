package com.xl.admin.controller;

import com.xl.domain.ResponseResult;
import com.xl.domain.dto.LinkDto;
import com.xl.domain.entity.Link;
import com.xl.domain.service.LinkService;
import com.xl.domain.utils.BeanCopyUtils;
import com.xl.domain.vo.LinkVo;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author xxll
 */
@RestController
@RequestMapping("/content/link")
public class LinkController {
    @Autowired
    private LinkService linkService;
    @GetMapping("list")
    public ResponseResult list(Integer pageNum,Integer pageSize,String name,String status){
        return linkService.listAllLink(pageNum,pageSize,name,status);
    }
    @GetMapping("{id}")
    public ResponseResult getById(@PathVariable Long id){
        return linkService.selectById(id);
    }
    @PutMapping
    public ResponseResult update(@RequestBody LinkVo linkVo){
        Link link = BeanCopyUtils.copyBean(linkVo, Link.class);
        linkService.updateById(link);
        return ResponseResult.okResult();
    }
    @DeleteMapping("{id}")
    public ResponseResult delete(@PathVariable Long id){
        linkService.removeById(id);
        return ResponseResult.okResult();
    }
    @PostMapping()
    public ResponseResult save(@RequestBody LinkVo linkVo){
        Link link = BeanCopyUtils.copyBean(linkVo, Link.class);
        linkService.save(link);
        return ResponseResult.okResult();
    }
    @PutMapping("changeLinkStatus")
    public ResponseResult change(@RequestBody LinkDto linkDto){
        Link link = BeanCopyUtils.copyBean(linkDto, Link.class);
        linkService.updateById(link);
        return ResponseResult.okResult();
    }
}
