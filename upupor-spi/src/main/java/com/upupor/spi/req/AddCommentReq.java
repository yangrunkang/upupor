package com.upupor.spi.req;

import lombok.Data;

/**
 * @author: YangRunkang(cruise)
 * @created: 2019/12/22 16:38
 */
@Data
public class AddCommentReq {

    private String targetId;
    private Integer commentSource;
    private String commentContent;
    private String replyToUserId;
}
