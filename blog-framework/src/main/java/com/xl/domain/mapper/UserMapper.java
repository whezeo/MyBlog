package com.xl.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xl.domain.entity.User;
import org.apache.ibatis.annotations.Mapper;


/**
 * 用户表(User)表数据库访问层
 *
 * @author makejava
 * @since 2022-12-23 21:05:13
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}

