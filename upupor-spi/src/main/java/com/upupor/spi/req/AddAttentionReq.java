package com.upupor.spi.req;

import lombok.Data;

/**
 * 添加关注请求
 *
 * @author YangRunkang(cruise)
 * @date 2020/02/01 21:24
 */
@Data
public class AddAttentionReq {

    private String attentionUserId;

    /**
     * true:已经收藏  false:未收藏
     */
    private Boolean isAttention;

}
