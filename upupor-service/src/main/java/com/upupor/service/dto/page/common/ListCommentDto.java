package com.upupor.service.dto.page.common;

import com.github.pagehelper.PageInfo;
import com.upupor.service.dao.entity.Comment;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 评论列表(评论内容通用)
 *
 * @author YangRunkang(cruise)
 * @date 2020/01/21 09:59
 */
@Data
public class ListCommentDto extends BaseListDto {

    private List<Comment> commentList;

    public ListCommentDto(PageInfo pageInfo) {
        super(pageInfo);
        this.commentList = new ArrayList<>();
    }

    public ListCommentDto() {
        this.commentList = new ArrayList<>();
    }
}
