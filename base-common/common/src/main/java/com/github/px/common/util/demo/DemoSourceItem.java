package com.github.px.common.util.demo;
;
import lombok.Data;

import java.util.Date;


/**
 * <p></p>
 *
 * @author <a href="mailto:xipan@bigvisiontech.com">panxi</a>
 * @version 1.0.0
 * @date 2020/3/17 10:24
 * @since 1.0
 */
@Data
public class DemoSourceItem {
    public DemoSourceItem(String name, Integer age, Date now) {
        this.name = name;
        this.age = age;
        this.now = now;
    }

    private String name;

    private Integer age;

    private Date now;



}
