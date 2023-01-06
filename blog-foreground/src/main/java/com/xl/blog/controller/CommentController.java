package com.xl.blog.controller;


import com.xl.domain.ResponseResult;
import com.xl.domain.dto.CommentDto;
import com.xl.domain.entity.Comment;
import com.xl.domain.enums.SystemConstants;
import com.xl.domain.service.CommentService;
import com.xl.domain.utils.BeanCopyUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author xxll
 */
@RestController
@RequestMapping("/comment")
@Api(tags = "评论",description = "评论相关接口")
public class CommentController {
    @Autowired
    CommentService commentService;

    @GetMapping("/commentList")
    public ResponseResult commentList(Long articleId, Integer pageNum, Integer pageSize){
        return commentService.commentList(SystemConstants.ARTICLE_COMMENT,articleId,pageNum,pageSize);
    }
    @ApiOperation(value = "友联评论列表",notes = "获取一页的评论")
    @GetMapping("/linkCommentList")
    @ApiImplicitParams({
            @ApiImplicitParam(name="pageNum",value="页号"),
            @ApiImplicitParam(name="pageSize",value="每页的大小")
    })
    public ResponseResult linkCommentList(Integer pageNum,Integer pageSize){
        return commentService.commentList(SystemConstants.LINK_COMMENT,null,pageNum,pageSize);
    }
    @PostMapping
    public ResponseResult saveComment(@RequestBody CommentDto commentDto){
        Comment comment = BeanCopyUtils.copyBean(commentDto, Comment.class);
        return commentService.addComment(comment);
    }
}
