/*
 * MIT License
 *
 * Copyright (c) 2021-2022 yangrunkang
 *
 * Author: yangrunkang
 * Email: yangrunkang53@gmail.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.upupor.service.business.profile;

import com.upupor.data.dto.page.MemberIndexDto;
import com.upupor.data.dto.page.ad.AbstractAd;
import com.upupor.data.dto.page.common.ListContentDto;
import com.upupor.data.types.ContentStatus;
import com.upupor.data.types.ViewTargetType;
import com.upupor.service.base.ContentService;
import com.upupor.service.base.TagService;
import com.upupor.service.business.profile.dto.Query;
import com.upupor.service.outer.req.ListContentReq;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static com.upupor.framework.CcConstant.ProfileView.PROFILE_CONTENT;

/**
 * @author Yang Runkang (cruise)
 * @date 2021年12月27日 21:01
 * @email: yangrunkang53@gmail.com
 */
@Component
@RequiredArgsConstructor
public class ContentProfile extends AbstractProfile {
    private final ContentService contentService;
    private final TagService tagService;

    @Override
    public ViewTargetType viewTargetType() {
        return ViewTargetType.PROFILE_CONTENT;
    }

    @Override
    protected void setSpecifyData(Query query) {
        String userId = query.getUserId();
        Integer pageNum = query.getPageNum();
        Integer pageSize = query.getPageSize();
        String tagName = query.getTagName();

        ListContentReq listContentReq = new ListContentReq();
        listContentReq.setUserId(userId);
        listContentReq.setStatus(ContentStatus.NORMAL);
        listContentReq.setPageNum(pageNum);
        listContentReq.setPageSize(pageSize);

        MemberIndexDto memberIndexDto = getMemberIndexDto();
        memberIndexDto.setTagName(tagName);
        memberIndexDto.setListContentDto(new ListContentDto());

        if (StringUtils.isNotEmpty(tagName)) {
            List<String> tagIdList = tagService.getTagListByName(tagName);
            if (CollectionUtils.isEmpty(tagIdList)) {
                return;
            }
            listContentReq.setTagIdList(tagIdList);
        }


        memberIndexDto.setListContentDto(contentService.listContent(listContentReq));
    }

    @Override
    protected void addAd() {
        ListContentDto listContentDto = getMemberIndexDto().getListContentDto();
        AbstractAd.ad(listContentDto.getContentList());
    }


    @Override
    public String viewName() {
        return PROFILE_CONTENT;
    }

}
