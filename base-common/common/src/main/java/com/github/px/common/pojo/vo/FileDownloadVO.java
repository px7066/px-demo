package com.github.px.common.pojo.vo;

import com.github.px.common.constant.FileType;
import lombok.Data;

import java.util.Date;

@Data
public class FileDownloadVO {
    private String name;

    private FileType fileType;

    private String currentPath;

    private String downloadUrl;

    private String virtualUrl;

//    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date updateTime;
}
