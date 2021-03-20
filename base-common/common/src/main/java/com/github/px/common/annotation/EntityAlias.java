package com.github.px.common.annotation;

import java.lang.annotation.*;

/**
 * <p>pojo对象getId() => id </p>
 *
 * @author <a href="mailto:xipan@bigvisiontech.com">panxi</a>
 * @version 1.0.0
 * @date 2020/5/18 14:18
 * @since 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Inherited
@Documented
public @interface EntityAlias {
    String value() default "id";

    String[] child() default {};
}
