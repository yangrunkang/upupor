package com.upupor.spi.req;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

/**
 * @author: YangRunkang(cruise)
 * @created: 2019/12/22 16:44
 */
@Data
public class AddContentDetailReq {

    @Length(max = 256, message = "文章标题过长,最多可输入256个字")
    @NotEmpty(message = "标题不能为空")
    private String title;

    @Length(max = 1024, message = "简介内容过长,最多可输入1024个字")
    private String shortContent;

    private Integer contentType;

    private String content;

    @Length(max = 256, message = "标签过多,请减少标签数目")
    private String tagIds;

    /**
     * 操作 temp: 代表草稿
     */
    private String operation;

    private Integer originType;

    @Length(max = 1024, message = "转载链接过长,最多可输入1024个字")
    private String noneOriginLink;

    private String preContentId;


}
