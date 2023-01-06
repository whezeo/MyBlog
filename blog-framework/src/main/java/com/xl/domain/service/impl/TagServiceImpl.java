package com.xl.domain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xl.domain.ResponseResult;
import com.xl.domain.dto.TagDto;
import com.xl.domain.entity.Tag;
import com.xl.domain.enums.AppHttpCodeEnum;
import com.xl.domain.mapper.TagMapper;
import com.xl.domain.service.TagService;
import com.xl.domain.utils.BeanCopyUtils;
import com.xl.domain.vo.PageVo;
import com.xl.domain.vo.TagVo;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 标签(Tag)表服务实现类
 *
 * @author makejava
 * @since 2022-12-27 15:27:12
 */
@Service("tagService")
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {

    @Override
    public ResponseResult listByName(Integer pageNum, Integer pageSize, TagDto tagdto) {
        //条件
        LambdaQueryWrapper<Tag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.hasText(tagdto.getRemark()),Tag::getRemark,tagdto.getRemark());
        queryWrapper.like(StringUtils.hasText(tagdto.getName()),Tag::getName,tagdto.getName());
        //分页查询
        Page<Tag> tagPage = new Page<>(pageNum, pageSize);

        super.page(tagPage,queryWrapper);
        //封装返回
        PageVo pageVo = new PageVo(tagPage.getRecords(), tagPage.getTotal());
        return ResponseResult.okResult(pageVo);
    }

    @Override
    public void save(TagDto tagDto) {
        Tag tag = new Tag();
        tag.setName(tagDto.getName());
        tag.setRemark(tagDto.getRemark());
        this.save(tag);
    }

    @Override
    public void delete(Long id) {
        if(id==null)
        {
            return;
        }
        this.removeById(id);
    }

    @Override
    public ResponseResult getTagById(Long id) {
        Tag tag = this.getById(id);
        if(tag==null){
            return ResponseResult.errorResult(AppHttpCodeEnum.TAG_NOT_FIND);
        }
        TagVo tagVo = BeanCopyUtils.copyBean(tag, TagVo.class);
        return ResponseResult.okResult(tagVo);
    }

    @Override
    public void save(TagVo tagVo) {
        Tag tag = BeanCopyUtils.copyBean(tagVo, Tag.class);
        updateById(tag);
    }

    @Override
    public ResponseResult listAllTag() {
        LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(Tag::getId,Tag::getName);
        List<Tag> list = list(wrapper);
        List<TagVo> tagVos = BeanCopyUtils.copyBeanList(list, TagVo.class);
        return ResponseResult.okResult(tagVos);
    }
}

