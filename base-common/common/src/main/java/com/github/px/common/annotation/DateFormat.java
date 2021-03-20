package com.github.px.common.annotation;

import java.lang.annotation.*;

/**
 * <p>日期转换定义，用于CopyUtil</p>
 *
 * @author <a href="mailto:xipan@bigvisiontech.com">panxi</a>
 * @version 1.0.0
 * @date 2020/3/17 11:11
 * @since 1.0
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface DateFormat {
    String value() default "yyyy-MM-dd HH:mm:ss";
}
