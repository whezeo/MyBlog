package com.xl.admin.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.xl.domain.ResponseResult;
import com.xl.domain.entity.Category;
import com.xl.domain.enums.AppHttpCodeEnum;
import com.xl.domain.service.CategoryService;
import com.xl.domain.utils.BeanCopyUtils;
import com.xl.domain.utils.WebUtils;
import com.xl.domain.vo.CategoryVO;
import com.xl.domain.vo.ExcelCategoryVo;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * @author xxll
 */
@RestController
@RequestMapping("/content/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    @GetMapping("listAllCategory")
    public ResponseResult listAllCategory(){
        return categoryService.listAllCategory();
    }
    @GetMapping("/export")
    @PreAuthorize("@ps.hasPermissions('content:category:export')")
    public void export(HttpServletResponse response){
        try {
            //设置下载文件的请求头
            WebUtils.setDownLoadHeader("文章分类.xlsx",response);
            //获取需要导出的数据
            List<Category> list = categoryService.list();
            List<ExcelCategoryVo> excelCategoryVos = BeanCopyUtils.copyBeanList(list, ExcelCategoryVo.class);
            //把数据写入到Excel中
            EasyExcel.write(response.getOutputStream(), ExcelCategoryVo.class).autoCloseStream(Boolean.FALSE)
                    .sheet("分类导出").doWrite(excelCategoryVos);
        } catch (IOException e) {
            //如果出现异常也要响应json
            ResponseResult result = ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR);
            WebUtils.renderString(response, JSON.toJSONString(result));
        }
    }

    @GetMapping("/list")
    public ResponseResult list (Integer pageNum,Integer pageSize,String name ,String status){
        return categoryService.listCateGory(pageNum,pageSize,name,status);
    }
    @PostMapping
    public ResponseResult save(@RequestBody CategoryVO categoryVO){
        return categoryService.saveCategory(categoryVO);
    }
    @GetMapping("{id}")
    public ResponseResult getById(@PathVariable Long id){
        return categoryService.selectById(id);
    }

    @PutMapping
    public ResponseResult update(@RequestBody CategoryVO categoryVO){
        Category category = BeanCopyUtils.copyBean(categoryVO, Category.class);
        categoryService.updateById(category);
        return ResponseResult.okResult();
    }
    @DeleteMapping("{id}")
    public ResponseResult delete(@PathVariable Long id){
        categoryService.removeById(id);
        return ResponseResult.okResult();
    }
}
