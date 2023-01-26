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

package com.upupor.service.business.build_msg.abstracts;

import com.upupor.framework.BusinessException;
import com.upupor.framework.ErrorCode;
import com.upupor.framework.config.UpuporConfig;
import com.upupor.framework.utils.SpringContextUtils;
import com.upupor.service.business.build_msg.abstracts.dto.parent.MsgParamDto;

/**
 * 抽象构造消息内容逻辑
 *
 * @author Yang Runkang (cruise)
 * @createTime 2023-01-27 01:39
 * @email: yangrunkang53@gmail.com
 */
public abstract class AbstractBuildMsgContent<T extends MsgParamDto> {

    /**
     * 消息参数Dto
     */
    protected T tMsgParamDto;

    /**
     * 邮件需要网址
     */
    protected static final String EMAIL_NEED_WEBSITE = SpringContextUtils.getBean(UpuporConfig.class).getWebsite();

    /**
     * 标识业务消息类型
     *
     * @return
     */
    public abstract BusinessMsgType businessMsgType();

    /**
     * 构造消息内容
     *
     * @return
     */
    public String buildMsgContent(MsgParamDto tMsgParamDto, MsgType msgType) {
        init(tMsgParamDto);
        if (MsgType.INNER_MSG.equals(msgType)) {
            return buildEmailMsg();
        } else if (MsgType.EMAIL.equals(msgType)) {
            return buildEmailMsg();
        }
        throw new BusinessException(ErrorCode.UNSUPPORT_UNKNOWN_MSG_CONTENT_BUILD);
    }

    private void init(MsgParamDto tMsgParamDto) {
        this.tMsgParamDto = ((T) tMsgParamDto);
    }


    /**
     * 构造站内信消息
     *
     * @return
     */
    protected abstract String buildInnerMsg();


    /**
     * 构造邮件消息
     *
     * @return
     */
    protected abstract String buildEmailMsg();


}
