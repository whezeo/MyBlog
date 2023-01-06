package com.xl.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xl.domain.entity.Comment;
import org.apache.ibatis.annotations.Mapper;


/**
 * 评论表(Comment)表数据库访问层
 *
 * @author makejava
 * @since 2022-12-24 20:47:46
 */

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {

}

