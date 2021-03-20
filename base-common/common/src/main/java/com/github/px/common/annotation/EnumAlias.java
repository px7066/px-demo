package com.github.px.common.annotation;

import java.lang.annotation.*;

/**
 * <p>CopyUtil标志为枚举</p>
 *
 * @author <a href="mailto:xipan@bigvisiontech.com">panxi</a>
 * @version 1.0.0
 * @date 2020/3/17 13:05
 * @since 1.0
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface EnumAlias {
    String value() default "name";

    boolean name() default false;
}
