package com.upupor.spi.req;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author: YangRunkang(cruise)
 * @created: 2019/12/22 16:44
 */
@Data
public class UpdateContentReq {

    @NotEmpty(message = "文章Id为空")
    private String contentId;

    private String title;

    private String shortContent;

    private Integer contentType;

    private Integer tagType;

    private String detailContent;

    private Integer status;

    private String userId;

    /**
     * 标签
     */
    private String tagIds;

    /**
     * 编辑原因
     */
    private String editReason;

    private Integer originType;
    private String noneOriginLink;

    /**
     * 是否将草稿公开 true-公开
     */
    private Boolean isDraftPublic;

}
