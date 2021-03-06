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
import com.upupor.service.types.ContentStatus;
import com.upupor.service.types.ContentType;
import com.upupor.service.types.OriginType;
import com.upupor.service.types.PinnedStatus;
import lombok.Data;

import java.util.List;
import java.util.Objects;

import static com.upupor.framework.CcConstant.Time.CONTENT_UPDATE_TIME;
import static com.upupor.framework.CcConstant.Time.NEW_CONTENT_TIME;

/**
 * ??????
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
     * ???????????? 0-?????? 1-?????? 2-?????? 3-?????? 4-?????? 5-??????
     */
    private ContentType contentType;

    /**
     * ????????????,?????????","??????
     */
    private String tagIds;

    /**
     * ?????? 0-?????? 1-??????[?????????] 2-????????? 3-?????? 4-?????? 5-????????? 6-???????????????
     */
    private ContentStatus status;


    /**
     * ????????????
     */
    private PinnedStatus pinnedStatus;

    /**
     * ????????????
     */
    private Integer editTimes;
    /**
     * ????????????
     */
    private Long editTime;


    private OriginType originType;
    private String noneOriginLink;

    /**
     * ??????Id
     */
    private Integer statementId;

    private Long createTime;
    /**
     * ?????????????????????
     */
    private Long latestCommentTime;
    /**
     * ??????????????????
     */
    private String latestCommentUserId;


    /**
     * ?????????
     */
    private String keywords;


    /**********************???DB??????*****************************/
    public static List<Integer> manageStatusList = Lists.newArrayList(
            ContentStatus.EXCEPTION.getStatus(), ContentStatus.DELETED.getStatus()
    );

    @TableField(exist = false)
    private ContentExtend contentExtend;

    /**
     * ????????????
     **/
    @TableField(exist = false)
    private ContentData contentData;

    @TableField(exist = false)
    private ContentEditReason contentEditReason;

    /**
     * ?????????????????? ????????????-?????????
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
     * ??????????????????
     */
    @TableField(exist = false)
    private String sysUpdateDate;

    /**
     * ??????????????????-?????????
     */
    @TableField(exist = false)
    private List<TagDto> tagDtoList;

    /**
     * ????????????
     *
     * @return
     */
    @TableField(exist = false)
    private ListCommentDto listCommentDto;

    /**
     * ??????
     */
    @TableField(exist = false)
    private Member member;
    /**
     * ??????
     */
    @TableField(exist = false)
    private String contentTypeDesc;

    /**
     * ?????????
     */
    @TableField(exist = false)
    private Integer collectTotal;

    /**
     * ?????????
     */
    @TableField(exist = false)
    private Integer commentTotal;

    /**
     * ?????????
     */
    @TableField(exist = false)
    private Integer collectNum;

    @TableField(exist = false)
    private Statement statement;

    /**
     * ???????????????
     */
    @TableField(exist = false)
    private List<Member> likesMemberList;

    /**
     * ????????????????????????????????????
     */
    @TableField(exist = false)
    private LastAndNextContentDto lastAndNextContentDto;

    /**
     * ???????????????
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
        // ????????????????????????
        content.setContentExtend(
                ContentExtend.create(
                        contentId,
                        createContentReq.getContent(),
                        createContentReq.getMdContent()
                )
        );

        // ????????????
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
     * ????????????????????????
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
            return "??????";
        }
        return byContentType.getName();
    }

    /**
     * ?????????????????????
     */
    @TableField(exist = false)
    private Boolean hasLatestEdit = Boolean.FALSE;

    public Boolean getHasLatestEdit() {
        if (Objects.isNull(editTime)) {
            return hasLatestEdit;
        }
        // ???????????????????????????????????????
        return CcDateUtil.getCurrentTime() - editTime <= CONTENT_UPDATE_TIME;
    }

    /**
     * ???????????????????????????
     */
    @TableField(exist = false)
    private Boolean newContent = Boolean.FALSE;

    public Boolean getNewContent() {
        if (Objects.isNull(createTime)) {
            return newContent;
        }
        return CcDateUtil.getCurrentTime() - createTime <= NEW_CONTENT_TIME;
    }

    @TableField(exist = false)
    private Boolean hasDraft = Boolean.FALSE;
}
