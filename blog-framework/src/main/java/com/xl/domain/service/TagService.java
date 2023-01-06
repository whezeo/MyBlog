package com.xl.domain.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xl.domain.ResponseResult;
import com.xl.domain.dto.TagDto;
import com.xl.domain.entity.Tag;
import com.xl.domain.vo.TagVo;


/**
 * 标签(Tag)表服务接口
 *
 * @author makejava
 * @since 2022-12-27 15:27:12
 */
public interface TagService extends IService<Tag> {

    ResponseResult listByName(Integer pageNum, Integer pageSize, TagDto tagdto);

    void save(TagDto tagDto);

    void delete(Long id);

    ResponseResult getTagById(Long id);

    void save(TagVo tagVo);

    ResponseResult listAllTag();
}

