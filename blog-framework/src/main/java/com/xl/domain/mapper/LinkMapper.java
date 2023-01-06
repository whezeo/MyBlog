package com.xl.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xl.domain.entity.Link;
import org.apache.ibatis.annotations.Mapper;


/**
 * 友链(Link)表数据库访问层
 *
 * @author makejava
 * @since 2022-12-23 15:34:10
 */
@Mapper
public interface LinkMapper extends BaseMapper<Link> {

}

