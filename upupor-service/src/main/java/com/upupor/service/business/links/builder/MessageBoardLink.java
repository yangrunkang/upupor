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
import com.upupor.service.business.links.abstracts.dto.MessageBoardLinkParamDto;

/**
 * 留言板消息
 *
 * @author Yang Runkang (cruise)
 * @createTime 2023-01-27 02:10
 * @email: yangrunkang53@gmail.com
 */
public class MessageBoardLink extends AbstractBuildLink<MessageBoardLinkParamDto> {
    @Override
    protected String buildInnerLink() {
        final String MESSAGE_BOARD_MSG = "<a style='cursor: pointer;position: relative;text-decoration: none;font-weight: bold;color: #000000;' href = '/profile/%s/message?msgId=%s#comment_%s'>%s</a>";
        return String.format(MESSAGE_BOARD_MSG, linkParamDto.getTargetUserId(), linkParamDto.getMsgId(), linkParamDto.getFloorNum(), linkParamDto.getTitle());
    }

    @Override
    protected String buildEmailLink() {
        final String MESSAGE_BOARD_EMAIL = "<a style='cursor: pointer;position: relative;text-decoration: none;font-weight: bold;color: #000000;' href = '" + EMAIL_NEED_WEBSITE + "/profile/%s/message?msgId=%s#comment_%s'>%s</a>";
        return String.format(MESSAGE_BOARD_EMAIL, linkParamDto.getTargetUserId(), linkParamDto.getMsgId(), linkParamDto.getFloorNum(), linkParamDto.getTitle());
    }

    @Override
    public BusinessLinkType businessLinkType() {
        return BusinessLinkType.MESSAGE_BOARD;
    }
}
