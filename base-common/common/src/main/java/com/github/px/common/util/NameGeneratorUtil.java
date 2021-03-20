package com.github.px.common.util;

import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>重命名服务</p>
 * @author <a href="mailto:7066450@qq.com">panxi</a>
 * @version 1.0.0
 * @since 1.0
 */
public class NameGeneratorUtil {
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    private static String lastTime = "";

    private static AtomicInteger atomicInteger = new AtomicInteger(0);

    /**
     * 单机下线程安全
     * 根据当前时间生成
     * @param name 文件名
     * @return String
     */
    public static String generateByTime(String name){
        String nowStr;
        synchronized (NameGeneratorUtil.class){
            nowStr = LocalDateTime.now().format(TIME_FORMATTER);
            if(!StringUtils.isEmpty(name)){
                nowStr = nowStr + "-" + name;
            }
            if(nowStr.equals(lastTime)){
                nowStr = nowStr + "-" + atomicInteger.incrementAndGet();
            }else {
                lastTime = nowStr;
                atomicInteger.set(0);
            }
        }
        return nowStr;
    }

    /**
     * 根据当前时间生成
     * @param name 文件名
     * @return String
     */
    public static String generateByDate(String name){
        String nowStr = LocalDate.now().format(DATE_TIME_FORMATTER);
        return nowStr + "-" + name;
    }

    public static void main(String[] args) {
        System.out.println(NameGeneratorUtil.generateByTime("file"));
    }
}
