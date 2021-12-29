package com.upupor.service.business.aggregation.service;

import com.upupor.service.dao.entity.Comment;
import com.upupor.service.dao.entity.Content;
import com.upupor.service.dao.entity.Radio;
import com.upupor.service.dto.page.common.ListCommentDto;
import com.upupor.spi.req.AddCommentReq;
import com.upupor.spi.req.ListCommentReq;

import java.util.List;

/**
 * 评论服务
 *
 * @author: YangRunkang(cruise)
 * @created: 2019/12/22 07:57
 */
public interface CommentService {
    /**
     * 添加评论
     *
     * @param addCommentReq
     * @return
     */
    Comment toComment(AddCommentReq addCommentReq);

    /**
     * 更新评论
     *
     * @param comment
     * @return
     */
    Boolean update(Comment comment);


    /**
     * 获取评论列表
     *
     * @param listCommentReq
     * @return
     */
    ListCommentDto listComment(ListCommentReq listCommentReq);

    /**
     * 根据评论id获取评论
     *
     * @param commentId
     * @return
     */
    Comment getCommentByCommentId(String commentId);

    /**
     * 评论总数
     *
     * @return
     */
    Integer total();

    /**
     * 绑定评论的用户名
     *
     * @param commentList
     */
    void bindCommentUserName(List<Comment> commentList);

    void bindContentComment(Content content, Integer pageNum, Integer pageSize);

    void bindRadioComment(Radio radio, Integer pageNum, Integer pageSize);

    /**
     * 根据目标id统计评论数
     *
     * @param targetId
     */
    Integer countByTargetId(String targetId);

}