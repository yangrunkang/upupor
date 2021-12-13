package com.upupor.spi.req;

import lombok.Data;

/**
 * @author: YangRunkang(cruise)
 * @created: 2019/12/22 16:41
 */
@Data
public class ListCommentReq {
    private String targetId;
    private String userId;
    private String commentId;
    private Integer status;
    /**
     * 评论来源和文章类型是一致的
     */
    private Integer commentSource;
    private Integer pageNum;
    private Integer pageSize;

}
