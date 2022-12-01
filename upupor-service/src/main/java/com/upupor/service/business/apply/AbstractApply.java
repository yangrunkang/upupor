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

package com.upupor.service.business.apply;

import com.upupor.data.dao.entity.Apply;
import com.upupor.data.dao.entity.enhance.ApplyEnhance;
import com.upupor.framework.utils.CcUtils;
import com.upupor.service.base.ApplyService;
import com.upupor.service.business.message.MessageSend;
import com.upupor.service.business.message.model.MessageModel;

import javax.annotation.Resource;

import static com.upupor.framework.CcConstant.NOTIFY_ADMIN;

/**
 * 抽象申请
 *
 * @author Yang Runkang (cruise)
 * @date 2022年01月02日 20:06
 * @email: yangrunkang53@gmail.com
 */
public abstract class AbstractApply<T> {

    @Resource
    private ApplyService applyService;

    /**
     * 通知管理员
     *
     * @param applyEnhance
     */
    protected abstract void notifyAdministrator(ApplyEnhance applyEnhance);

    /**
     * 申请
     *
     * @param t
     * @return
     */
    protected abstract Apply apply(T t);

    protected void sendMessage(String title, String content) {
        MessageSend.send(MessageModel.builder()
                .toUserId(NOTIFY_ADMIN)
                .emailModel(MessageModel.EmailModel.builder()
                        .title(title)
                        .content(content)
                        .build())
                .messageId(CcUtils.getUuId())
                .build());
    }

    protected Boolean insertApply(Apply apply) {
        return applyService.addApply(apply) > 0;
    }

    public Boolean doBusiness(T t) {
        Apply apply = apply(t);
        notifyAdministrator(ApplyEnhance.builder().apply(apply).build());
        return Boolean.TRUE;
    }

}
