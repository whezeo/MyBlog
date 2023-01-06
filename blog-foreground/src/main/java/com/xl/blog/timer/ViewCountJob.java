package com.xl.blog.timer;

import com.xl.domain.entity.Article;
import com.xl.domain.service.ArticleService;
import com.xl.domain.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author xxll
 * Auto redisTomysqlViewCount
 */
@Component
public class ViewCountJob {
    @Autowired
    RedisCache redisCache;

    @Autowired
    private ArticleService articleService;
    @Scheduled(cron = "0 * * * * ?")
    public void redisToMysql(){
        Map<String, Integer> map = redisCache.getCacheMap("article:viewCount");
        List<Article> articleList = map.entrySet().stream()
                .map(entry -> new Article(Long.valueOf(entry.getKey()), entry.getValue().longValue()))
                .collect(Collectors.toList());
        articleService.updateBatchById(articleList);
    }
}
