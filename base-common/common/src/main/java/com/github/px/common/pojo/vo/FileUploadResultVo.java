package com.github.px.common.pojo.vo;

import lombok.Data;

/**
 * <p></p>
 *
 * @author <a href="mailto:xipan@bigvisiontech.com">panxi</a>
 * @version 1.0.0
 * @date 2020/5/7 13:15
 * @since 1.0
 */
@Data
public class FileUploadResultVo {
    private String id;
    /** mediaId */
    private String mediaId;
    /** 文件原始名称*/
    private String originalName;

    /** 存储名称*/
    private String storageName;

    /** 文件大小*/
    private Long size;

    /** 虚拟路径*/
    private String virtualPath;

    /** 存储路径*/
    private String storagePath;
}
