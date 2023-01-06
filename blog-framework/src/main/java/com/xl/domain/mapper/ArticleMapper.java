package com.xl.domain.mapper;

import com.xl.domain.entity.Article;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author liveb
* @description 针对表【sg_article(文章表)】的数据库操作Mapper
* @createDate 2022-12-22 15:12:50
* @Entity com.xl.domin.entity.Article
*/

@Mapper
public interface ArticleMapper extends BaseMapper<Article> {

}




