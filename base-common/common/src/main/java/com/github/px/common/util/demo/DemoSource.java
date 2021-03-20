package com.github.px.common.util.demo;

import lombok.Data;

import java.util.Date;
import java.util.Set;

/**
 * <p></p>
 *
 * @author <a href="mailto:xipan@bigvisiontech.com">panxi</a>
 * @version 1.0.0
 * @date 2020/3/4 15:24
 * @since 1.0
 */
@Data
public class DemoSource {
    private String name1;

    private String password;

    private Date now;

    private Set<DemoSourceItem> sourceItems;

    private Double dCount;

    private DemoSourceItem demoSourceItem;
}
