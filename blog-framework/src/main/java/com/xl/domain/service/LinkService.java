package com.xl.domain.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xl.domain.ResponseResult;
import com.xl.domain.entity.Link;


/**
 * 友链(Link)表服务接口
 *
 * @author makejava
 * @since 2022-12-23 15:34:10
 */
public interface LinkService extends IService<Link> {

    ResponseResult getAllLink();

    ResponseResult listAllLink(Integer pageNum, Integer pageSize, String name, String status);

    ResponseResult selectById(Long id);
}

