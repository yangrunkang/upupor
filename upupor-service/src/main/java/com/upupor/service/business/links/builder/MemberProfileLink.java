/*
 * <!--
 *   ~ MIT License
 *   ~
 *   ~ Copyright (c) 2021-2023 yangrunkang
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

package com.upupor.service.business.links.builder;

import com.upupor.service.business.links.abstracts.AbstractBuildLink;
import com.upupor.service.business.links.abstracts.BusinessLinkType;
import com.upupor.service.business.links.abstracts.dto.MemberProfileLinkParamDto;

/**
 * 用户主页
 *
 * @author Yang Runkang (cruise)
 * @createTime 2023-01-27 02:35
 * @email: yangrunkang53@gmail.com
 */
public class MemberProfileLink extends AbstractBuildLink<MemberProfileLinkParamDto> {
    @Override
    protected String buildInnerAnchorLink() {
        final String PROFILE_INNER_MSG = "<a" + A_LINK_STYLE + "href = '/profile/%s/content?msgId=%s'> %s </a>";
        return String.format(PROFILE_INNER_MSG, linkParamDto.getMemberUserId(), linkParamDto.getMsgId(), linkParamDto.getMemberUserName());
    }

    @Override
    protected String buildEmailAnchorLink() {
        final String PROFILE_EMAIL = "<a" + A_LINK_STYLE + "href = '" + EMAIL_NEED_WEBSITE + "/profile/%s/content?msgId=%s'> %s </a>";
        return String.format(PROFILE_EMAIL, linkParamDto.getMemberUserId(), linkParamDto.getMsgId(), linkParamDto.getMemberUserName());
    }

    @Override
    public BusinessLinkType businessLinkType() {
        return BusinessLinkType.MEMBER_PROFILE;
    }
}
