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

package com.upupor.service.scheduled.sitemap;

import com.upupor.framework.CcConstant;
import com.upupor.framework.config.UpuporConfig;
import com.upupor.service.data.dao.entity.Content;
import com.upupor.service.data.dao.entity.Member;
import com.upupor.service.data.aggregation.service.MemberService;
import com.upupor.service.dto.page.common.ListMemberDto;
import com.upupor.service.dto.seo.GoogleSeoDto;
import com.upupor.service.scheduled.ScheduledCommonService;
import com.upupor.service.types.MemberStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Yang Runkang (cruise)
 * @date 2022年03月13日 21:30
 * @email: yangrunkang53@gmail.com
 */
@Component
@RequiredArgsConstructor
public class MemberProfileSiteMap extends AbstractSiteMap<Member> {

    private final MemberService memberService;
    private final UpuporConfig upuporConfig;
    private final ScheduledCommonService scheduledCommonService;

    @Override
    protected Boolean dataCheck() {
        return memberService.total() > 0;
    }

    @Override
    protected List<Member> getSiteMapData() {
        return scheduledCommonService.memberList();
    }

    @Override
    protected void renderSiteMap(List<Member> memberList) {

        String webSite = upuporConfig.getWebsite();

        List<String> urlList = new ArrayList<>();
        urlList.add("/profile/%s/content");
        urlList.add("/profile/%s/radio");
        urlList.add("/profile/%s/message");
        urlList.add("/profile/%s/attention");
        urlList.add("/profile/%s/fans");

        memberList.forEach(member -> {
            if (member.getStatus().equals(MemberStatus.NORMAL)) {
                for (String url : urlList) {
                    GoogleSeoDto memberProfile = new GoogleSeoDto();
                    String memberProfileUrl = webSite + String.format(url, member.getUserId());
                    memberProfile.setLoc(memberProfileUrl);
                    memberProfile.setChangeFreq("hourly");
                    memberProfile.setLastmod(sdf.format(member.getSysUpdateTime()));
                    memberProfile.setPriority("0.5");// 默认值
                    googleSeoDtoList.add(memberProfile);
                }
            }
        });
    }
}