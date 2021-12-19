package com.upupor.service.dao.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.google.common.collect.Lists;
import com.upupor.framework.utils.CcDateUtil;
import com.upupor.service.common.CcConstant;
import com.upupor.service.common.CcEnum;
import com.upupor.service.dto.dao.LastAndNextContentDto;
import com.upupor.service.dto.page.common.ListCommentDto;
import com.upupor.service.dto.page.common.TagDto;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 内容
 *
 * @author: runkangyang (cruise)
 * @date: 2020.01.05 23:37
 */
@Data
public class Content {
    private Long id;

    private String contentId;

    private String userId;

    private String title;

    private String shortContent;

    private String picture;

    /**
     * 文章类型 0-默认 1-技术 2-问答 3-分享 4-职场 5-记录
     */
    private Integer contentType;

    /**
     * 文章标签,多个以","隔开
     */
    private String tagIds;

    /**
     * 状态 0-正常 1-草稿 2-审核中 3-异常 4-删除 5-回收站 6-仅自己可见
     */
    private Integer status;

    /**
     * 是否是第一次发布 0-未发布 1-已发布
     */
    private Integer isInitialStatus;

    /**
     * 置顶状态
     */
    private Integer pinnedStatus;

    /**
     * 编辑次数
     */
    private Integer editTimes;


    private Integer originType;
    private String noneOriginLink;

    /**
     * 声明Id
     */
    private Integer statementId;

    private Long createTime;
    /**
     * 最新回复的时间
     */
    private Long latestCommentTime;
    /**
     * 最新回复的人
     */
    private String latestCommentUserId;

    private Date sysUpdateTime;

    /**
     * 关键字
     */
    private String keywords;


    /**********************非DB字段*****************************/
    public static List<Integer> manageStatusList = Lists.newArrayList(
            CcEnum.ContentStatus.EXCEPTION.getStatus(), CcEnum.ContentStatus.DELETED.getStatus()
    );

    @TableField(exist = false)
    private ContentExtend contentExtend;

    /**
     * 文章数据
     **/
    @TableField(exist = false)
    private ContentData contentData;

    @TableField(exist = false)
    private ContentEditReason contentEditReason;

    /**
     * 页面冗余字段 创建日记-年月日
     */
    @TableField(exist = false)
    private String createDate;
    @TableField(exist = false)
    private String latestCommentDate;
    @TableField(exist = false)
    private String createDateDiff;

    /**
     * 页面冗余字段
     */
    @TableField(exist = false)
    private String sysUpdateDate;

    /**
     * 页面冗余字段-标签名
     */
    @TableField(exist = false)
    private List<TagDto> tagDtoList;

    /**
     * 评论内容
     *
     * @return
     */
    @TableField(exist = false)
    private ListCommentDto listCommentDto;

    /**
     * 用户
     */
    @TableField(exist = false)
    private Member member;

    /**
     * 收藏数
     */
    @TableField(exist = false)
    private Integer collectTotal;

    /**
     * 评论数
     */
    @TableField(exist = false)
    private Integer commentTotal;

    /**
     * 收藏数
     */
    @TableField(exist = false)
    private Integer collectNum;

    @TableField(exist = false)
    private Statement statement;

    /**
     * 点赞的用户
     */
    @TableField(exist = false)
    private List<Member> likesMemberList;

    /**
     * 绑定本文的上一篇及下一篇
     */
    @TableField(exist = false)
    private LastAndNextContentDto lastAndNextContentDto;

    /**
     * 文章访问者
     */
    @TableField(exist = false)
    private List<Viewer> viewerList;

    public Content() {
        this.tagIds = CcConstant.EMPTY_STR;
        this.listCommentDto = new ListCommentDto();
        this.collectTotal = 0;
        this.commentTotal = 0;
        this.collectNum = 0;
    }

    public void init(){
        this.setStatus(CcEnum.ContentStatus.NORMAL.getStatus());
        this.setIsInitialStatus(CcEnum.ContentIsInitialStatus.FIRST_PUBLISHED.getStatus());
        this.setEditTimes(0);
        this.setPinnedStatus(CcEnum.PinnedStatus.UN_PINNED.getStatus());
        this.setCreateTime(CcDateUtil.getCurrentTime());
        this.setLatestCommentTime(CcDateUtil.getCurrentTime());
        this.setSysUpdateTime(new Date());
    }


    public String getCreateDate() {
        if (Objects.isNull(createTime)) {
            return createDate;
        }
        return CcDateUtil.timeStamp2Date(createTime);
    }

    public String getLatestCommentDate() {
        if (Objects.isNull(latestCommentTime)) {
            return latestCommentDate;
        }
        return CcDateUtil.timeStamp2Date(latestCommentTime);
    }

    /**
     * 最新回复的人姓名
     */
    @TableField(exist = false)
    private String latestCommentUserName;

    @JSONField(serialize = false)
    public String getCreateDateDiff() {
        return CcDateUtil.snsFormat(createTime);
    }

}
