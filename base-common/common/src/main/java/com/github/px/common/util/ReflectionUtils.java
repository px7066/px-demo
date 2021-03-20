package com.github.px.common.util;

import com.github.px.common.pojo.vo.FileDownloadVO;

import java.lang.reflect.Field;

/**
 * <p>反射工具</p>
 *
 * @author <a href="mailto:xipan@bigvisiontech.com">panxi</a>
 * @version 1.0.0
 * @date 2020/11/13
 */
public class ReflectionUtils {
    /**
     * 反射给指定fieldName设置属性
     * @param fieldName fieldName
     * @param obj 对象
     */
    public static void setField(String fieldName, Object obj, Object val) {
        if(obj == null){
            return;
        }
        Class clazz = obj.getClass();
        Field field = getField(fieldName, clazz);
        if(field != null){
            if(!field.isAccessible()){
                field.setAccessible(true);
            }
            try {
                field.set(obj, val);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private static Field getField(String fieldName, Class clazz) {
        Field field = null;
        while (field == null && clazz != Object.class){
            try {
                field = clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                try {
                    field = clazz.getField(fieldName);
                } catch (NoSuchFieldException noSuchFieldException) {
                    clazz = clazz.getSuperclass();
                }
            }
        }
        return field;
    }

    public static Object getField(String fieldName, Object obj) {
        Field field = getField(fieldName, obj.getClass());
        if(field != null){
            try {
                if(!field.isAccessible()){
                    field.setAccessible(true);
                }
                return field.get(obj);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void main(String[] args) {
        FileDownloadVO fileDownloadVO = new FileDownloadVO();
        ReflectionUtils.setField("fileType", fileDownloadVO, null);

        System.out.println(fileDownloadVO);

    }
}
