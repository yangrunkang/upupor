package com.upupor.spi.req;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * 添加活动请求
 *
 * @author YangRunkang(cruise)
 * @date 2020/02/05 00:14
 */
@Data
public class SetKeywordsReq {

    @NotEmpty(message = "文章Id不能为空")
    private String contentId;

    @NotEmpty(message = "关键字不能为空")
    private String keywords;


}
