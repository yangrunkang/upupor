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

package com.upupor.service.business.build_msg.builder;

import com.upupor.service.business.build_msg.abstracts.AbstractBuildMsgContent;
import com.upupor.service.business.build_msg.abstracts.BusinessMsgType;
import com.upupor.service.business.build_msg.abstracts.dto.RadioMsgParamDto;

/**
 * 电台消息
 *
 * @author Yang Runkang (cruise)
 * @createTime 2023-01-27 02:10
 * @email: yangrunkang53@gmail.com
 */
public class RadioMsg extends AbstractBuildMsgContent<RadioMsgParamDto> {
    @Override
    protected String buildInnerMsg() {
        final String RADIO_MSG = "<a style='cursor: pointer;position: relative;text-decoration: none;font-weight: bold;color: #000000;' href = '/r/%s?msgId=%s'>%s</a>";
        return String.format(RADIO_MSG, tMsgParamDto.getRadioId(), tMsgParamDto.getMsgId(), tMsgParamDto.getRadioIntro());
    }

    @Override
    protected String buildEmailMsg() {
        final String RADIO_EMAIL = "<a style='cursor: pointer;position: relative;text-decoration: none;font-weight: bold;color: #000000;' href = '" + EMAIL_NEED_WEBSITE + "/r/%s?msgId=%s'>%s</a>";
        return String.format(RADIO_EMAIL, tMsgParamDto.getRadioId(), tMsgParamDto.getMsgId(), tMsgParamDto.getRadioIntro());
    }

    @Override
    public BusinessMsgType businessMsgType() {
        return BusinessMsgType.RADIO;
    }


}
