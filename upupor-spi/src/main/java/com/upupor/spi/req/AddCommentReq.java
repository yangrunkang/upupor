package com.upupor.spi.req;

import lombok.Data;

/**
 * @author: YangRunkang(cruise)
 * @created: 2019/12/22 16:38
 */
@Data
public class AddCommentReq {
    /**
     * 目标id
     */
    private String targetId;
    /**
     * 评论来源
     */
    private Integer commentSource;
    /**
     * 评论内容
     */
    private String commentContent;
    /**
     * 回复给用户
     */
    private String replyToUserId;
}
