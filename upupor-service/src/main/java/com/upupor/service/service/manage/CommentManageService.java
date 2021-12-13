package com.upupor.service.service.manage;

import com.upupor.service.dto.page.common.ListCommentDto;
import com.upupor.spi.req.ListCommentReq;

/**
 * 评论管理服务
 *
 * @author YangRunkang(cruise)
 * @date 2020/01/27 17:19
 */
public interface CommentManageService {


    /**
     * 获取评论列表
     *
     * @param listCommentReq
     * @return
     */
    ListCommentDto listComment(ListCommentReq listCommentReq);


}