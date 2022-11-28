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

package com.upupor.service.business.profile;

import com.google.common.collect.Lists;
import com.upupor.data.dao.entity.Content;
import com.upupor.data.dao.entity.Tag;
import com.upupor.data.dao.entity.enhance.ContentEnhance;
import com.upupor.data.dao.entity.enhance.MemberEnhance;
import com.upupor.data.dto.page.MemberIndexDto;
import com.upupor.data.types.ViewTargetType;
import com.upupor.framework.BusinessException;
import com.upupor.framework.CcConstant;
import com.upupor.framework.ErrorCode;
import com.upupor.framework.utils.CcUtils;
import com.upupor.service.base.*;
import com.upupor.service.business.profile.dto.Query;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.upupor.framework.thread.completable.CompletableFuturePool.ASYNC;

/**
 * 个人主页抽象
 *
 * @author Yang Runkang (cruise)
 * @date 2021年12月27日 21:00
 * @email: yangrunkang53@gmail.com
 */

public abstract class AbstractProfile {
    @Resource
    private ViewerService viewerService;
    @Resource
    private MemberService memberService;
    @Resource
    private FanService fanService;
    @Resource
    private AttentionService attentionService;
    @Resource
    private ContentService contentService;
    @Resource
    private TagService tagService;

    protected MemberIndexDto memberIndexDto = new MemberIndexDto();

    public MemberIndexDto getMemberIndexDto() {
        return memberIndexDto;
    }

    public MemberIndexDto getBusinessData(Query query) {
        String userId = query.getUserId();
        setMember(userId);
        setSpecifyData(query);
        recordViewer(userId);
        addAd();
        return memberIndexDto;
    }

    private void setMember(String userId) {
        MemberEnhance memberEnhance = memberService.memberInfo(userId);
        if (Objects.isNull(memberEnhance)) {
            throw new BusinessException(ErrorCode.MEMBER_NOT_EXISTS);
        }
        // 设置用户粉丝数
        CompletableFuture<Void> getFansNum = CompletableFuture.runAsync(() -> {
            int fansNum = fanService.getFansNum(memberEnhance.getMember().getUserId());
            memberEnhance.setFansNum(fansNum);
        }, ASYNC);

        // 设置用户关注数
        CompletableFuture<Void> getAttentionNum = CompletableFuture.runAsync(() -> {
            Integer attentionNum = attentionService.getAttentionNum(memberEnhance.getMember().getUserId());
            memberEnhance.setAttentionNum(attentionNum);
        }, ASYNC);

        // 设置文章总数
        CompletableFuture<Void> getUserTotalContentNum = CompletableFuture.runAsync(() -> {
            Integer totalContentNum = contentService.getUserTotalContentNum(memberEnhance.getMember().getUserId());
            memberEnhance.setTotalContentNum(totalContentNum);
        }, ASYNC);

        // 获取用户总积分值
        CompletableFuture<Void> getTotalIntegral = CompletableFuture.runAsync(() -> {
            Integer totalIntegral = memberService.totalIntegral(memberEnhance.getMember().getUserId());
            memberEnhance.setTotalIntegral(totalIntegral);
        }, ASYNC);

        // 设置标签
        CompletableFuture<Void> setTagList = CompletableFuture.runAsync(() -> {
            memberIndexDto.setTagList(getUserTagList(userId));
        }, ASYNC);

        // 设置当前用户是否已关注该profile
        CompletableFuture<Void> setCurrUserIsAttention = CompletableFuture.runAsync(() -> {
            memberIndexDto.setCurrUserIsAttention(contentService.currentUserIsAttentionAuthor(userId));
        }, ASYNC);

        // 设置用户声明
        CompletableFuture<Void> bindStatement = CompletableFuture.runAsync(() -> {
            memberService.bindStatement(memberEnhance);
        }, ASYNC);

        CompletableFuture<Void> allCompletableFuture = CompletableFuture.allOf(
                getFansNum,
                getAttentionNum,
                getUserTotalContentNum,
                getTotalIntegral,
                setTagList,
                setCurrUserIsAttention,
                bindStatement
        );

        allCompletableFuture.join();

        // 获取用户属性
        memberIndexDto.setMember(memberEnhance);
    }

    /**
     * 记录浏览者
     *
     * @param userId
     */
    private void recordViewer(String userId) {
        // 记录访问者
        viewerService.addViewer(userId, viewTargetType());
        // 设置访问者
        memberIndexDto.setViewerList(viewerService.listViewerByTargetIdAndType(userId, viewTargetType()));
    }

    /**
     * 添加广告
     */
    protected abstract void addAd();

    /**
     * 浏览者类型
     *
     * @return
     */
    public abstract ViewTargetType viewTargetType();

    /**
     * 视图
     *
     * @return
     */
    public abstract String viewName();

    /**
     * 获取数据
     *
     * @param query@return
     */
    protected abstract void setSpecifyData(Query query);


    private List<Tag> getUserTagList(String userId) {

        // 获取用户标签
        List<ContentEnhance> contentEnhanceList = contentService.listAllByUserId(Lists.newArrayList(userId));

        if (CollectionUtils.isEmpty(contentEnhanceList)) {
            return new ArrayList<>();
        }

        List<String> tagIdList = contentEnhanceList.stream()
                .map(ContentEnhance::getContent)
                .map(Content::getTagIds)
                .distinct().collect(Collectors.toList());
        if (CollectionUtils.isEmpty(tagIdList)) {
            return new ArrayList<>();
        }
        String s = CcUtils.convertListToStr(tagIdList);
        String[] split = s.split(CcConstant.COMMA);
        if (ArrayUtils.isEmpty(split)) {
            return new ArrayList<>();
        }

        List<Tag> tags = tagService.listByTagIdList(Arrays.asList(split));
        if (CollectionUtils.isEmpty(tags)) {
            return new ArrayList<>();
        }
        return tags;
    }

}
