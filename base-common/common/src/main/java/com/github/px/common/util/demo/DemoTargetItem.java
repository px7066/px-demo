package com.github.px.common.util.demo;

import com.github.px.common.annotation.EnumAlias;
import lombok.Data;

/**
 * <p></p>
 *
 * @author <a href="mailto:xipan@bigvisiontech.com">panxi</a>
 * @version 1.0.0
 * @date 2020/3/17 10:24
 * @since 1.0
 */
@Data
public class DemoTargetItem {
    private String name;

    private Integer age;

    private String now;

    @EnumAlias
    private String gender;
}
