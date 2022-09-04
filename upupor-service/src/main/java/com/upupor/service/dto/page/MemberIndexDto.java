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

package com.upupor.service.dto.page;

import com.upupor.service.data.dao.entity.Member;
import com.upupor.service.data.dao.entity.Statement;
import com.upupor.service.data.dao.entity.Tag;
import com.upupor.service.data.dao.entity.ViewHistory;
import com.upupor.service.dto.page.common.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户首页Dto-用户管理
 *
 * @author YangRunkang(cruise)
 * @date 2020/01/19 23:54
 */
@Data
public class MemberIndexDto {

    /**
     * 用户信息
     */
    private Member member;

    /**
     * 内容列表对象
     */
    private ListContentDto listContentDto;

    /**
     * 审核对象
     */
    private ListApplyDto listApplyDto;

    /**
     * 单个审核对象Dto
     */
    private ApplyDto applyDto;

    /**
     * 评论对象
     */
    private ListCommentDto listCommentDto;

    /**
     * 收藏对象
     */
    private ListCollectDto listCollectDto;

    /**
     * 关注对象
     */
    private ListAttentionDto listAttentionDto;

    /**
     * 粉丝对象
     */
    private ListFansDto listFansDto;

    /**
     * 消息
     */
    private ListMessageDto listMessageDto;

    /**
     * 积分记录
     */
    private ListIntegralDto listIntegralDto;

    private ListCssPatternDto listCssPatternDto;

    private ListRadioDto listRadioDto;
    /**
     * 作者标签
     * (作者写过的文章中做包含的标签)
     */
    private List<Tag> tagList;
    /**
     * 作者是否已经被你关注
     */
    private Boolean currUserIsAttention;
    /**
     * 备注
     */
    private Statement statement;
    /**
     * 文章访问者
     */
    private List<ViewHistory> viewerList;

    /**
     * 标签名
     */
    private String tagName;

    public MemberIndexDto() {
        this.member = new Member();
        this.listContentDto = new ListContentDto();
        this.listApplyDto = new ListApplyDto();
        this.listCommentDto = new ListCommentDto();
        this.listCollectDto = new ListCollectDto();
        this.listAttentionDto = new ListAttentionDto();
        this.listFansDto = new ListFansDto();
        this.listMessageDto = new ListMessageDto();
        this.listIntegralDto = new ListIntegralDto();
        this.listRadioDto = new ListRadioDto();

        this.tagList = new ArrayList<>();
        this.viewerList = new ArrayList<>();
        this.currUserIsAttention = false;
    }

}
