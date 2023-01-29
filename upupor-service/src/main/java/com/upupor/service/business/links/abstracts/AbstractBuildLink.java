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

package com.upupor.service.business.links.abstracts;

import com.upupor.framework.BusinessException;
import com.upupor.framework.ErrorCode;
import com.upupor.framework.config.UpuporConfig;
import com.upupor.framework.utils.SpringContextUtils;
import com.upupor.service.business.links.abstracts.dto.parent.LinkParamDto;

import java.util.Objects;

/**
 * 抽象构造链接内容逻辑
 *
 * @author Yang Runkang (cruise)
 * @createTime 2023-01-27 01:39
 * @email: yangrunkang53@gmail.com
 */
public abstract class AbstractBuildLink<T extends LinkParamDto> {
    protected static final String A_LINK_STYLE = " style='cursor: pointer;position: relative;text-decoration: none;font-weight: bold;color: #000000;' ";

    /**
     * 链接参数Dto
     */
    protected T linkParamDto;

    /**
     * 邮件需要网址
     */
    protected static final String EMAIL_NEED_WEBSITE = SpringContextUtils.getBean(UpuporConfig.class).getWebsite();

    /**
     * 标识业务链接类型
     *
     * @return
     */
    public abstract BusinessLinkType businessLinkType();

    /**
     * 构造链接内容
     *
     * @return
     */
    public String buildLink(LinkParamDto linkParamDto, MsgType msgType) {
        init(linkParamDto);
        if (MsgType.INNER_MSG.equals(msgType)) {
            return isCommentOperation() ? buildInnerAnchorLink() : buildInnerLink();
        } else if (MsgType.EMAIL.equals(msgType)) {
            return isCommentOperation() ? buildEmailAnchorLink() : buildEmailLink();
        }
        throw new BusinessException(ErrorCode.UNSUPPORT_UNKNOWN_LINK_CONTENT_BUILD);
    }

    /**
     * 是否是评论操作,有些是点赞的操作,只需要发送站内信通知,不是评论,因此没有楼层这个概念
     *
     * @return
     */
    public Boolean isCommentOperation() {
        return Objects.nonNull(linkParamDto.getFloorNum());
    }

    private void init(LinkParamDto linkParamDto) {
        this.linkParamDto = ((T) linkParamDto);
    }


    /**
     * 构造站内信锚点链接-评论锚点
     *
     * @return
     */
    protected abstract String buildInnerAnchorLink();

    /**
     * 构造邮件锚点链接-评论锚点
     *
     * @return
     */
    protected abstract String buildEmailAnchorLink();

    /**
     * 构造站内信链接
     *
     * @return
     */
    protected String buildInnerLink() {
        return buildInnerAnchorLink();
    }

    /**
     * 构造邮件链接
     *
     * @return
     */
    protected String buildEmailLink() {
        return buildEmailAnchorLink();
    }


}
