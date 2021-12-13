package com.upupor.spi.req;

import lombok.Data;

/**
 * 进入编辑器请求
 *
 * @author YangRunkang(cruise)
 * @date 2020/01/12 12:35
 */
@Data
public class GetEditorReq {
    /**
     * 来源(即从哪个页面进入浏览器)
     */
    private Integer contentType;

    /**
     * 标签ID
     */
    private String tag;

    /**
     * 内容id
     */
    private String contentId;

}

