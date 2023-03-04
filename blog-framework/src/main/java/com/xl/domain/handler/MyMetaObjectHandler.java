package com.xl.domain.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.xl.domain.utils.SecurityUtils;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * mp自动填充字段
 * @author liveb
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        Long userId = null;
        try {
            userId = SecurityUtils.getUserId();
        } catch (Exception e) {
            e.printStackTrace();
            userId = 0L;
            //表示是自己创建
        }
        if(userId==null){
            userId=0L;
        }
        this.setFieldValByName("createTime", new Date(), metaObject);
        this.setFieldValByName("createBy",userId, metaObject);
        this.setFieldValByName("updateTime", new Date(), metaObject);
        this.setFieldValByName("updateBy", userId, metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        Long userId = null;
        try {
            userId = SecurityUtils.getUserId();
        } catch (Exception e) {
            e.printStackTrace();
            userId = 0L;
            //表示是自己创建
        }
        if(userId==null){
            userId=0L;
        }
        this.setFieldValByName("updateTime", new Date(), metaObject);
        this.setFieldValByName(" ", userId, metaObject);
    }
}
