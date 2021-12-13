package com.upupor.service.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.upupor.service.dao.entity.Comment;
import com.upupor.service.dto.dao.CommentNumDto;
import com.upupor.spi.req.ListCommentReq;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CommentMapper extends BaseMapper<Comment> {


    List<Comment> list(ListCommentReq listCommentReq);

    List<Comment> listManage(ListCommentReq listCommentReq);

    Integer total();

    List<CommentNumDto> selectByCommentIdList(@Param("list") List<String> commentIdList);

    Integer countByTargetId(@Param("targetId") String targetId);
}
