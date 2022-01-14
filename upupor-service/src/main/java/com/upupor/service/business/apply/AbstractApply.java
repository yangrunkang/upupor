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

import com.upupor.service.business.aggregation.dao.entity.Apply;
import com.upupor.service.business.aggregation.service.ApplyService;
import com.upupor.service.business.aggregation.service.MessageService;
import com.upupor.service.common.CcConstant;

import javax.annotation.Resource;

import static com.upupor.service.common.CcConstant.SKIP_SUBSCRIBE_EMAIL_CHECK;

/**
 * 抽象申请
 *
 * @author Yang Runkang (cruise)
 * @date 2022年01月02日 20:06
 * @email: yangrunkang53@gmail.com
 */
public abstract class AbstractApply<T> {

    @Resource
    private MessageService messageService;

    @Resource
    private ApplyService applyService;

    private Apply applyEntity = null;

    /**
     * 通知管理员
     */
    protected abstract void notifyAdministrator(Apply apply);

    /**
     * 申请
     *
     * @param t
     * @return
     */
    protected abstract Boolean apply(T t);

    protected void sendEmail(String title, String content) {
        messageService.sendEmail(CcConstant.UPUPOR_EMAIL, title, content, SKIP_SUBSCRIBE_EMAIL_CHECK);
    }

    protected Boolean apply(Apply apply) {
        applyEntity = apply;
        return applyService.addApply(apply) > 0;
    }

    public Boolean doBusiness(T t){
        Boolean success = apply(t);
        notifyAdministrator(applyEntity);
        return success;
    }

}
