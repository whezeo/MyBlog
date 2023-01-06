package com.xl.domain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xl.domain.ResponseResult;
import com.xl.domain.dto.AddArticleDto;
import com.xl.domain.entity.Article;
import com.xl.domain.entity.ArticleTag;
import com.xl.domain.entity.Category;
import com.xl.domain.enums.SystemConstants;
import com.xl.domain.service.ArticleService;
import com.xl.domain.mapper.ArticleMapper;
import com.xl.domain.service.ArticleTagService;
import com.xl.domain.service.CategoryService;
import com.xl.domain.utils.BeanCopyUtils;
import com.xl.domain.utils.RedisCache;
import com.xl.domain.vo.ArticleDetailVo;
import com.xl.domain.vo.ArticleListVo;
import com.xl.domain.vo.HotArticleVo;
import com.xl.domain.vo.PageVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
* @author liveb
* @description 针对表【sg_article(文章表)】的数据库操作Service实现
* @createDate 2022-12-22 15:12:50
*/

@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article>
    implements ArticleService{

    @Autowired
    private ArticleTagService articleTagService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private RedisCache redisCache;
    @Override
    public ResponseResult getHotArticleList() {
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        //status 为 0
        wrapper.eq(Article::getStatus, SystemConstants.ARTICLE_STATUS_NORMAL);
        // 降序排列
        wrapper.orderByDesc(Article::getViewCount);
        //10条
        Page<Article> articlePage = new Page<>(1, 10);
        super.page(articlePage,wrapper);
        List<Article> articleList = articlePage.getRecords();
//        ArrayList<HotArticleVo> hotArticleVos = new ArrayList<>();
//        for (Article article : articleList) {
//            HotArticleVo hotArticleVo = new HotArticleVo();
//            BeanUtils.copyProperties(article,hotArticleVo);
//            hotArticleVos.add(hotArticleVo);
//        }
        //redis中查询viewCount
        articleList.forEach(article -> {
            Integer viewCount = redisCache.getCacheMapValue("article:viewCount", article.getId().toString());
            article.setViewCount(viewCount.longValue());
        });

        List<HotArticleVo> hotArticleVos =
                BeanCopyUtils.copyBeanList(articleList, HotArticleVo.class);
        return ResponseResult.okResult(hotArticleVos);
    }

    @Override
    public ResponseResult articleList(Integer pageNum, Integer pageSize, Long categoryId) {
        //查询条件
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        // 如果 有categoryId 就要 查询时要和传入的相同
        queryWrapper.eq(categoryId!=0,Article::getCategoryId,categoryId);
        // 状态是正式发布的 0
        queryWrapper.eq(Article::getStatus,SystemConstants.ARTICLE_STATUS_NORMAL);
        // 对isTop进行降序
        queryWrapper.orderByDesc(Article::getIsTop);
        //分页查询
        Page<Article> articlePage = new Page<>(pageNum,pageSize);
        super.page(articlePage,queryWrapper);
        //查询categoryName
        List<Article> records = articlePage.getRecords();
        records.forEach((article -> {
            Integer viewCount = redisCache.getCacheMapValue("article:viewCount", article.getId().toString());
            article.setViewCount(viewCount.longValue());
            article.setCategoryName(categoryService.getById(article.getCategoryId()).getName());
        }));
        //封装查询结果
        List<ArticleListVo> articleListVos = BeanCopyUtils.copyBeanList(records, ArticleListVo.class);
        PageVo pageVo = new PageVo(articleListVos,articlePage.getTotal());
        return ResponseResult.okResult(pageVo);
    }

    @Override
    public ResponseResult getArticleDetail(Long id) {
        /**
         * 根据id查询
         * redis查询viewCount
         * 转换为vo
         * 根据id查询分类名
         * 封装响应返回
         */
        Article article = super.getById(id);
        Integer viewCount = redisCache.getCacheMapValue("article:viewCount", article.getId().toString());
        article.setViewCount(viewCount.longValue());
        ArticleDetailVo articleDetailVo = BeanCopyUtils.copyBean(article, ArticleDetailVo.class);
        Long categoryId = articleDetailVo.getCategoryId();
        Category category = categoryService.getById(categoryId);
        articleDetailVo.setCategoryName(category.getName());
        return ResponseResult.okResult(articleDetailVo);
    }

    @Override
    public ResponseResult updateViewCount(Long id) {
        //更新数据
        redisCache.incrementCacheMapValue("article:viewCount",id.toString(),1);
        return ResponseResult.okResult();
    }

    @Override
    @Transactional
    public ResponseResult add(AddArticleDto article) {
        Article article1 = BeanCopyUtils.copyBean(article, Article.class);
        this.save(article1);
        List<ArticleTag> articleTags = article
                .getTags().stream()
                .map(tagId -> new ArticleTag(article1.getId(), tagId)).collect(Collectors.toList());
        articleTagService.saveBatch(articleTags);

        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult list(Integer pageNum, Integer pageSize, AddArticleDto articleDto) {

        //条件查询
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.hasText(articleDto.getTitle()),Article::getTitle,articleDto.getTitle());
        queryWrapper.like(StringUtils.hasText(articleDto.getSummary()),Article::getSummary,articleDto.getSummary());
        Page<Article> page = new Page<>();
        this.page(page,queryWrapper);
        List<Article> records = page.getRecords();
        List<AddArticleDto> addArticleDtos = BeanCopyUtils.copyBeanList(records, AddArticleDto.class);
        PageVo pageVo = new PageVo(addArticleDtos, page.getTotal());
        //封装返回
        return ResponseResult.okResult(pageVo);
    }

    @Override
    public ResponseResult selectArticleById(Long id) {
        Article article = this.getById(id);
        AddArticleDto articleDto = BeanCopyUtils.copyBean(article, AddArticleDto.class);
        LambdaQueryWrapper<ArticleTag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ArticleTag::getArticleId,id);
        List<ArticleTag> list = articleTagService.list(queryWrapper);
        List<Long> tags = list.stream().map(ArticleTag::getTagId).collect(Collectors.toList());
        articleDto.setTags(tags);
        return ResponseResult.okResult(articleDto);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateArticle(AddArticleDto articleDto) {
        Article article = BeanCopyUtils.copyBean(articleDto, Article.class);
        //更新文章
        this.updateById(article);
        Long articleDtoId = articleDto.getId();
        LambdaQueryWrapper<ArticleTag> queryWrapper = new LambdaQueryWrapper<>();
        //删除源标签
        queryWrapper.eq(ArticleTag::getArticleId,articleDtoId);
        articleTagService.remove(queryWrapper);
        //新增标签
        List<Long> tags = articleDto.getTags();
        List<ArticleTag> articleTags =
                tags.stream().map(tag -> new ArticleTag(articleDtoId, tag)).collect(Collectors.toList());
        articleTagService.saveBatch(articleTags);

    }

    @Override
    public void deleteById(Long id) {
        //逻辑删除
        this.removeById(id);
    }
}




