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

package com.upupor.service.business.links;

import com.upupor.framework.BusinessException;
import com.upupor.framework.ErrorCode;
import com.upupor.service.business.links.abstracts.AbstractBuildLink;
import com.upupor.service.business.links.abstracts.BusinessLinkType;
import com.upupor.service.business.links.abstracts.MsgType;
import com.upupor.service.business.links.abstracts.dto.parent.LinkParamDto;
import com.upupor.service.business.links.builder.ContentLink;
import com.upupor.service.business.links.builder.MemberProfileLink;
import com.upupor.service.business.links.builder.MessageBoardLink;
import com.upupor.service.business.links.builder.RadioLink;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Yang Runkang (cruise)
 * @createTime 2023-01-27 02:41
 * @email: yangrunkang53@gmail.com
 */
public class LinkBuilderInstance {
    final static List<AbstractBuildLink<? extends LinkParamDto>> abstractBuildLinkList = new ArrayList<>();

    static {
        abstractBuildLinkList.add(new ContentLink());
        abstractBuildLinkList.add(new MessageBoardLink());
        abstractBuildLinkList.add(new MemberProfileLink());
        abstractBuildLinkList.add(new RadioLink());
    }


    public static String buildLink(BusinessLinkType businessMsgType, LinkParamDto msgParamDto, MsgType msgType) {
        for (AbstractBuildLink<? extends LinkParamDto> abstractBuildLink : abstractBuildLinkList) {
            if (businessMsgType.equals(abstractBuildLink.businessLinkType())) {
                return abstractBuildLink.buildLink(msgParamDto, msgType);
            }
        }
        throw new BusinessException(ErrorCode.UNSUPPORT_UNKNOWN_BUSINESS_LINK_CONTENT_BUILD);
    }


}
