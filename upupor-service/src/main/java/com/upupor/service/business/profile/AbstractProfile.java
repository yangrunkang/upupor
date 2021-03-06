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
import com.upupor.framework.BusinessException;
import com.upupor.framework.CcConstant;
import com.upupor.framework.ErrorCode;
import com.upupor.framework.utils.CcUtils;
import com.upupor.service.business.profile.dto.Query;
import com.upupor.service.data.dao.entity.Content;
import com.upupor.service.data.dao.entity.Member;
import com.upupor.service.data.dao.entity.Tag;
import com.upupor.service.data.service.*;
import com.upupor.service.dto.page.MemberIndexDto;
import com.upupor.service.types.ViewTargetType;
import org.springframework.util.CollectionUtils;
import org.thymeleaf.util.ArrayUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.upupor.framework.thread.completable.CompletableFuturePool.ASYNC;

/**
 * ??????????????????
 *
 * @author Yang Runkang (cruise)
 * @date 2021???12???27??? 21:00
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
        Member member = memberService.memberInfo(userId);
        if (Objects.isNull(member)) {
            throw new BusinessException(ErrorCode.MEMBER_NOT_EXISTS);
        }
        // ?????????????????????
        CompletableFuture<Void> getFansNum = CompletableFuture.runAsync(() -> {
            int fansNum = fanService.getFansNum(member.getUserId());
            member.setFansNum(fansNum);
        }, ASYNC);

        // ?????????????????????
        CompletableFuture<Void> getAttentionNum = CompletableFuture.runAsync(() -> {
            Integer attentionNum = attentionService.getAttentionNum(member.getUserId());
            member.setAttentionNum(attentionNum);
        }, ASYNC);

        // ??????????????????
        CompletableFuture<Void> getUserTotalContentNum = CompletableFuture.runAsync(() -> {
            Integer totalContentNum = contentService.getUserTotalContentNum(member.getUserId());
            member.setTotalContentNum(totalContentNum);
        }, ASYNC);

        // ????????????????????????
        CompletableFuture<Void> getTotalIntegral = CompletableFuture.runAsync(() -> {
            Integer totalIntegral = memberService.totalIntegral(member.getUserId());
            member.setTotalIntegral(totalIntegral);
        }, ASYNC);

        // ????????????
        CompletableFuture<Void> setTagList = CompletableFuture.runAsync(() -> {
            memberIndexDto.setTagList(getUserTagList(userId));
        }, ASYNC);

        // ????????????????????????????????????profile
        CompletableFuture<Void> setCurrUserIsAttention = CompletableFuture.runAsync(() -> {
            memberIndexDto.setCurrUserIsAttention(contentService.currentUserIsAttentionAuthor(userId));
        }, ASYNC);

        // ??????????????????
        CompletableFuture<Void> bindStatement = CompletableFuture.runAsync(() -> {
            memberService.bindStatement(member);
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

        // ??????????????????
        memberIndexDto.setMember(member);
    }

    /**
     * ???????????????
     *
     * @param userId
     */
    private void recordViewer(String userId) {
        // ???????????????
        viewerService.addViewer(userId, viewTargetType());
        // ???????????????
        memberIndexDto.setViewerList(viewerService.listViewerByTargetIdAndType(userId, viewTargetType()));
    }

    /**
     * ????????????
     */
    protected abstract void addAd();

    /**
     * ???????????????
     *
     * @return
     */
    public abstract ViewTargetType viewTargetType();

    /**
     * ??????
     *
     * @return
     */
    public abstract String viewName();

    /**
     * ????????????
     *
     * @param query@return
     */
    protected abstract void setSpecifyData(Query query);


    private List<Tag> getUserTagList(String userId) {

        // ??????????????????
        List<Content> contentList = contentService.listAllByUserId(Lists.newArrayList(userId));

        if (CollectionUtils.isEmpty(contentList)) {
            return new ArrayList<>();
        }

        List<String> tagIdList = contentList.stream()
                .map(Content::getTagIds).distinct().collect(Collectors.toList());
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
