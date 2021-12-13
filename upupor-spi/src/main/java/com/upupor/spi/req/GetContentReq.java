package com.upupor.spi.req;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author: YangRunkang(cruise)
 * @created: 2019/12/22 16:45
 */
@Data
public class GetContentReq {

    private String userId;

    @NotEmpty(message = "文章ID不能为空")
    private String contentId;

    public GetContentReq() {
    }

    public GetContentReq(String contentId) {
        this.contentId = contentId;
    }
}
