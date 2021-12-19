package com.upupor.spi.req;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * 添加置顶请求
 *
 * @author: cruise
 * @created: 2020/06/23 19:25
 */
@Data
public class PinnedReq {

    @NotEmpty(message = "文章ID不能为空")
    private String contentId;

    private Integer pinnedStatus;

}
