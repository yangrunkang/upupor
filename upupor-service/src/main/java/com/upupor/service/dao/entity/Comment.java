package com.upupor.service.dao.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.upupor.framework.utils.CcDateUtil;
import lombok.Data;

import java.util.Date;

@Data
public class Comment extends BaseEntity {

    private String userId;

    private String commentId;

    private String targetId;

    private Integer commentSource;

    private Integer status;

    private Integer agree;

    private Integer likeNum;

    private Long createTime;

    private Date sysUpdateTime;

    private String commentContent;

    /*******************/

    /**
     * 被评论的文章标题
     */
    @TableField(exist = false)
    private Content content;

    /**
     * 评论者自己
     */
    @TableField(exist = false)
    private Member member;

    /**
     * 创建时间
     */
    @TableField(exist = false)
    private String createDate;
    @TableField(exist = false)
    private String createDateDiff;

    public String getCreateDate() {
        return CcDateUtil.timeStamp2Date(createTime);
    }

    @JSONField(serialize = false)
    public String getCreateDateDiff() {
        return CcDateUtil.snsFormat(createTime);
    }

}
