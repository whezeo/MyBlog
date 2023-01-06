package com.xl.blog.timer;

import com.xl.domain.entity.Article;
import com.xl.domain.service.ArticleService;
import com.xl.domain.utils.RedisCache;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * springboot启动时初始化代码
 */
@Component
public class RedisRunner implements CommandLineRunner {
    @Autowired
    RedisCache redisCache;
    @Autowired
    ArticleService articleService;
    @Override
    public void run(String... args) throws Exception {
        List<Article> list = articleService.list();
        Map<String,Integer> map = list.stream()
                .collect(Collectors.toMap
                (article -> article.getId().toString()
                        ,article -> article.getViewCount().intValue()));
        redisCache.setCacheMap("article:viewCount",map);
    }
}
