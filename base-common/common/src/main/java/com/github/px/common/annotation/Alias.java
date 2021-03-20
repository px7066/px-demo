package com.github.px.common.annotation;

import java.lang.annotation.*;

/**
 * <p>别名(使用CopyUtil的复制功能时使用)</p>
 * @see com.bigvision.blemon.common.util.CopyUtil
 *
 * @author <a href="mailto:xipan@bigvisiontech.com">panxi</a>
 * @version 1.0.0
 * @date 2020/3/4 15:33
 * @since 1.0
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Alias {
    String value();
}
