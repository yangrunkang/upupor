/*
 * <!--
 *   ~ MIT License
 *   ~
 *   ~ Copyright (c) 2021-2022 yangrunkang
 *   ~
 *   ~ Author: yangrunkang
 *   ~ Email: yangrunkang53@gmail.com
 *   ~
 *   ~ Permission is hereby granted, free of charge, to any person obtaining a copy
 *   ~ of this software and associated documentation files (the "Software"), to deal
 *   ~ in the Software without restriction, including without limitation the rights
 *   ~ to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *   ~ copies of the Software, and to permit persons to whom the Software is
 *   ~ furnished to do so, subject to the following conditions:
 *   ~
 *   ~ The above copyright notice and this permission notice shall be included in all
 *   ~ copies or substantial portions of the Software.
 *   ~
 *   ~ THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   ~ IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *   ~ FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *   ~ AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *   ~ LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *   ~ OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *   ~ SOFTWARE.
 *   -->
 */

package com.upupor.service.data.dao.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.google.common.collect.Lists;
import com.upupor.framework.CcConstant;
import com.upupor.framework.utils.CcDateUtil;
import com.upupor.framework.utils.CcUtils;
import com.upupor.service.dto.dao.LastAndNextContentDto;
import com.upupor.service.dto.page.common.ListCommentDto;
import com.upupor.service.dto.page.common.TagDto;
import com.upupor.service.outer.req.content.CreateContentReq;
import com.upupor.service.types.*;
import lombok.Data;

import java.util.List;
import java.util.Objects;

import static com.upupor.framework.CcConstant.Time.CONTENT_UPDATE_TIME;
import static com.upupor.framework.CcConstant.Time.NEW_CONTENT_TIME;

/**
 * 内容
 *
 * @author: runkangyang (cruise)
 * @date: 2020.01.05 23:37
 */
@Data
public class Content extends BaseEntity {

    private String contentId;

    private String userId;

    private String title;

    private String shortContent;

    private String picture;

    /**
     * 文章类型 0-默认 1-技术 2-问答 3-分享 4-职场 5-记录
     */
    private ContentType contentType;

    /**
     * 文章标签,多个以","隔开
     */
    private String tagIds;

    /**
     * 状态 0-正常 1-草稿 2-审核中 3-异常 4-删除 5-回收站 6-仅自己可见
     */
    private ContentStatus status;

    /**
     * 是否是第一次发布 0-未发布 1-已发布
     */
    private ContentIsInitialStatus isInitialStatus;

    /**
     * 置顶状态
     */
    private PinnedStatus pinnedStatus;

    /**
     * 编辑次数
     */
    private Integer editTimes;
    /**
     * 编辑时间
     */
    private Long editTime;


    private OriginType originType;
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


    /**
     * 关键字
     */
    private String keywords;


    /**********************非DB字段*****************************/
    public static List<Integer> manageStatusList = Lists.newArrayList(
            ContentStatus.EXCEPTION.getStatus(), ContentStatus.DELETED.getStatus()
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
    @TableField(exist = false)
    private String editDate;

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
     * 用户
     */
    @TableField(exist = false)
    private String contentTypeDesc;

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
        init();
    }

    public static Content empty() {
        return new Content();
    }

    public static Content create(String contentId, CreateContentReq createContentReq) {
        Content content = new Content();
        content.setContentId(contentId);
        content.setTitle(createContentReq.getTitle());
        content.setContentType(createContentReq.getContentType());
        content.setShortContent(createContentReq.getShortContent());
        content.setTagIds(CcUtils.removeLastComma(createContentReq.getTagIds()));
        // 初始化文章拓展表
        content.setContentExtend(
                ContentExtend.create(
                        contentId,
                        createContentReq.getContent(),
                        createContentReq.getMdContent()
                )
        );

        // 原创处理
        if (Objects.nonNull(createContentReq.getOriginType())) {
            content.setOriginType(createContentReq.getOriginType());
            content.setNoneOriginLink(createContentReq.getNoneOriginLink());
        }

        return content;
    }

    private void init() {
        this.collectTotal = 0;
        this.commentTotal = 0;
        this.collectNum = 0;
        this.listCommentDto = new ListCommentDto();
        this.tagIds = CcConstant.EMPTY_STR;
        this.status = ContentStatus.NORMAL;
        this.isInitialStatus = ContentIsInitialStatus.FIRST_PUBLISHED;
        this.editTimes = 0;
        this.pinnedStatus = PinnedStatus.UN_PINNED;
        this.originType = OriginType.ORIGIN;
        this.createTime = CcDateUtil.getCurrentTime();
        this.latestCommentTime = CcDateUtil.getCurrentTime();
    }


    public String getCreateDate() {
        if (Objects.isNull(createTime)) {
            return createDate;
        }
        return CcDateUtil.timeStamp2Date(createTime);
    }

    public String getEditDate() {
        if (Objects.isNull(editTime)) {
            return editDate;
        }
        return CcDateUtil.timeStamp2Date(editTime);
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
        return CcDateUtil.timeStamp2DateOnly(createTime);
    }

    public String getContentTypeDesc() {
        ContentType byContentType = ContentType.getByContentType(contentType);
        if (Objects.isNull(byContentType)) {
            return "内容";
        }
        return byContentType.getName();
    }

    /**
     * 最近是否更新过
     */
    @TableField(exist = false)
    private Boolean hasLatestEdit = Boolean.FALSE;

    public Boolean getHasLatestEdit() {
        if (Objects.isNull(editTime)) {
            return hasLatestEdit;
        }
        // 在一天内更新都算最新更新过
        return CcDateUtil.getCurrentTime() - editTime <= CONTENT_UPDATE_TIME;
    }

    /**
     * 是否是最近的新文章
     */
    @TableField(exist = false)
    private Boolean newContent = Boolean.FALSE;

    public Boolean getNewContent() {
        if (Objects.isNull(createTime)) {
            return newContent;
        }
        return CcDateUtil.getCurrentTime() - createTime <= NEW_CONTENT_TIME;
    }
}
