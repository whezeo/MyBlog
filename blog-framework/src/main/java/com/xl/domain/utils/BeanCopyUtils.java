package com.xl.domain.utils;

import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

public class BeanCopyUtils {

    private BeanCopyUtils(){
    }

    public static <V> V copyBean(Object source , Class<V> clazz){
        try {
            V instance = clazz.newInstance();
            BeanUtils.copyProperties(source,instance);
            return instance;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <V,U> List<V> copyBeanList(List<U> sources,Class<V> clazz){
        return sources.stream().map(
                o->copyBean(o,clazz)
        ).collect(Collectors.toList());
    }

    public static void main(String[] args) {

    }
}
