package com.github.px.common.util.demo;


import com.github.px.common.annotation.*;
import lombok.Data;

import java.util.List;

/**
 * <p></p>
 *
 * @author <a href="mailto:xipan@bigvisiontech.com">panxi</a>
 * @version 1.0.0
 * @date 2020/3/4 15:24
 * @since 1.0
 */
@Data
public class DemoTarget {
    @Alias("name1")
    private String name;

    @Ignore
    private String password;

    @DateFormat("yyyy-MM-dd")
    private String now;

    @Alias("sourceItems")
    private List<DemoTargetItem> targetItems;

    private String gender;

    @NumberFormat(pattern = "#.000")
    private Double dCount;

    @Alias("demoSourceItem")
    @EntityAlias("name")
    private String itemName;
}
