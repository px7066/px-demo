package com.github.px.common.util;


import org.reflections.Reflections;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>类工具</p>
 *
 * @author <a href="mailto:xipan@bigvisiontech.com">panxi</a>
 * @version 1.0.0
 * @date 2020/12/25
 */
public class ClassUtils {

    public final static Map<String, Reflections> maps = new ConcurrentHashMap<>();

    /**
     * 查询指定包名，类名，父类下面的class
     * @param packageName 包名
     * @param simpleName 类名
     * @param superClass 父类
     * @return class
     */
    public static Class searchBySimpleName(String packageName, String simpleName, Class superClass){
        if(StringUtils.isEmpty(packageName)){
            return null;
        }
        if(maps.containsKey(packageName)){
            Reflections reflections = maps.get(packageName);
            if(reflections != null){
                Set<Class> allClasses = reflections.getSubTypesOf(superClass);
                if(!CollectionUtils.isEmpty(allClasses)){
                    for (Class allClass : allClasses) {
                        if(allClass.getSimpleName().equals(simpleName)){
                            return allClass;
                        }
                    }
                }
            }
        }else {
            Reflections reflections = new Reflections(packageName);
            Set<Class> allClasses = reflections.getSubTypesOf(superClass);
            if(!CollectionUtils.isEmpty(allClasses)){
                for (Class allClass : allClasses) {
                    if(allClass.getSimpleName().equals(simpleName)){
                        return allClass;
                    }
                }
            }
        }
        return null;
    }

    public static Set<Class> findAllByPackage(String packageName, Class superClass){
        if(StringUtils.isEmpty(packageName)){
            return null;
        }
        if(maps.containsKey(packageName)){
            Reflections reflections = maps.get(packageName);
            if(reflections != null){
                return reflections.getSubTypesOf(superClass);
            }
        }else {
            Reflections reflections = new Reflections(packageName);
            return reflections.getSubTypesOf(superClass);
        }
        return Collections.emptySet();
    }

    public static <T> T createByClass(Class<T> tClass){
        try {
            Constructor<T> constructor = tClass.getConstructor();
            constructor.setAccessible(true);
            return tClass.newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Field> findFieldsByAnnotation(Class clazz, Class<? extends Annotation> type) {
        List<Field> fieldArray = new ArrayList<>();
        while (clazz != Object.class){
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                Annotation annotation = field.getDeclaredAnnotation(type);
                if(annotation != null){
                    fieldArray.add(field);
                }
            }
            clazz = clazz.getSuperclass();
        }
        return fieldArray;
    }
}
