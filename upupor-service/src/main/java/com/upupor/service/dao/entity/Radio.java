package com.upupor.service.dao.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.upupor.framework.utils.CcDateUtil;
import com.upupor.service.dto.page.common.ListCommentDto;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Data
public class Radio {
    private Long id;

    private String radioId;

    private String userId;

    private String contentId;

    private Integer status;

    private Long createTime;

    private Date sysUpdateTime;

    private String radioIntro;

    /**
     * 最新回复的时间
     */
    private Long latestCommentTime;
    private String radioUrl;

    /**
     * 最新回复的人
     */
    private String latestCommentUserId;
    /****************************************/
    /**
     * 当前电台上传状态
     */
    @TableField(exist = false)
    private Integer uploadStatus;

    /**
     * 评论数
     */
    @TableField(exist = false)
    private Integer commentTotal;

    /**
     * 电台创作者
     */
    @TableField(exist = false)
    private Member member;

    /**
     * 评论内容
     *
     * @return
     */
    @TableField(exist = false)
    private ListCommentDto listCommentDto;

    /**
     * 文章数据
     */
    @TableField(exist = false)
    private ContentData contentData;

    @TableField(exist = false)
    private String createDate;

    @TableField(exist = false)
    private String latestCommentDate;

    @TableField(exist = false)
    private String latestCommentUserName;

    @TableField(exist = false)
    private List<Viewer> viewerList;

    public String getLatestCommentDate() {
        if (Objects.isNull(latestCommentTime)) {
            return latestCommentDate;
        }
        return CcDateUtil.timeStamp2Date(latestCommentTime);
    }


    public String getCreateDate() {
        return CcDateUtil.timeStamp2Date(createTime);
    }

}
