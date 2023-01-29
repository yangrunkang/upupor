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
import com.upupor.service.business.links.abstracts.dto.ContentLinkParamDto;

/**
 * 内容消息
 *
 * @author Yang Runkang (cruise)
 * @createTime 2023-01-27 02:10
 * @email: yangrunkang53@gmail.com
 */
public class ContentLink extends AbstractBuildLink<ContentLinkParamDto> {

    @Override
    protected String buildInnerAnchorLink() {
        final String CONTENT_INNER_MSG = "<a" + A_LINK_STYLE + "href = '/u/%s?msgId=%s#comment_%s'>%s</a>";
        return String.format(CONTENT_INNER_MSG, linkParamDto.getContentId(), linkParamDto.getMsgId(), linkParamDto.getFloorNum(), linkParamDto.getContentTitle());
    }

    @Override
    protected String buildEmailAnchorLink() {
        final String CONTENT_EMAIL = "<a" + A_LINK_STYLE + "href = '" + EMAIL_NEED_WEBSITE + "/u/%s?msgId=%s#comment_%s'>%s</a>";
        return String.format(CONTENT_EMAIL, linkParamDto.getContentId(), linkParamDto.getMsgId(), linkParamDto.getFloorNum(), linkParamDto.getContentTitle());
    }

    @Override
    protected String buildInnerLink() {
        final String CONTENT_INNER_MSG = "<a" + A_LINK_STYLE + "href = '/u/%s?msgId=%s'>%s</a>";
        return String.format(CONTENT_INNER_MSG, linkParamDto.getContentId(), linkParamDto.getMsgId(), linkParamDto.getContentTitle());
    }

    @Override
    protected String buildEmailLink() {
        final String CONTENT_EMAIL = "<a" + A_LINK_STYLE + "href = '" + EMAIL_NEED_WEBSITE + "/u/%s?msgId=%s'>%s</a>";
        return String.format(CONTENT_EMAIL, linkParamDto.getContentId(), linkParamDto.getMsgId(), linkParamDto.getContentTitle());
    }

    @Override
    public BusinessLinkType businessLinkType() {
        return BusinessLinkType.CONTENT;
    }
}
