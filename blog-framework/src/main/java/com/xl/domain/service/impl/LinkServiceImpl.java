package com.xl.domain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xl.domain.ResponseResult;
import com.xl.domain.entity.Link;
import com.xl.domain.enums.SystemConstants;
import com.xl.domain.mapper.LinkMapper;
import com.xl.domain.service.LinkService;
import com.xl.domain.utils.BeanCopyUtils;
import com.xl.domain.vo.LinkVo;
import com.xl.domain.vo.PageVo;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 友链(Link)表服务实现类
 *
 * @author makejava
 * @since 2022-12-23 15:34:10
 */
@Service("linkService")
public class LinkServiceImpl extends ServiceImpl<LinkMapper, Link> implements LinkService {

    @Override
    public ResponseResult getAllLink() {

        //查询所有审核通过的
        LambdaQueryWrapper<Link> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Link::getStatus, SystemConstants.LINK_STATUS_NORMAL);
        List<Link> list = super.list(queryWrapper);
        //转换成vo
        List<LinkVo> linkVos = BeanCopyUtils.copyBeanList(list, LinkVo.class);
        //封装返回
        return ResponseResult.okResult(linkVos);
    }

    @Override
    public ResponseResult listAllLink(Integer pageNum, Integer pageSize, String name, String status) {
        LambdaQueryWrapper<Link> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.hasText(name),Link::getName,name);
        queryWrapper.eq(StringUtils.hasText(status),Link::getStatus,status);
        Page<Link> page = new Page<>();
        this.page(page,queryWrapper);
        List<Link> records = page.getRecords();
        List<LinkVo> linkVos = BeanCopyUtils.copyBeanList(records, LinkVo.class);
        PageVo pageVo = new PageVo(linkVos, page.getTotal());

        return ResponseResult.okResult(pageVo);
    }

    @Override
    public ResponseResult selectById(Long id) {
        Link link = getById(id);
        LinkVo linkVo = BeanCopyUtils.copyBean(link, LinkVo.class);

        return ResponseResult.okResult(linkVo);
    }
}

