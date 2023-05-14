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
import com.upupor.data.dto.page.apply.ApplyContentDto;
import com.upupor.data.types.ApplySource;
import com.upupor.data.types.ApplyStatus;
import com.upupor.framework.BusinessException;
import com.upupor.framework.ErrorCode;
import com.upupor.framework.utils.CcDateUtil;
import com.upupor.framework.utils.CcUtils;
import com.upupor.framework.utils.JsonUtils;
import com.upupor.service.outer.req.AddTagReq;
import com.upupor.service.utils.SessionUtils;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author Yang Runkang (cruise)
 * @date 2022年01月02日 20:08
 * @email: yangrunkang53@gmail.com
 */
@Component
public class TagApply extends AbstractApply<AddTagReq> {
    @Override
    protected void notifyAdministrator(ApplyEnhance applyEnhance) {

        String emailContent = "申请id:%s" + "\n" +
                "用户id:%s" + "\n" +
                "申请页面:%s" + "\n" +
                "申请标签:%s" + "\n" +
                "申请子标签:%s";

        String applyIntro = applyEnhance.getApplyContentDto().getApplyIntro();
        AddTagReq addTagReq = JsonUtils.parse2Clazz(applyIntro, AddTagReq.class);
        emailContent = String.format(emailContent, applyEnhance.getApply().getApplyId(),
                applyEnhance.getApply().getUserId(),
                addTagReq.getPageName(),
                addTagReq.getTagName(),
                addTagReq.getChildTagName()
        );

        sendMessage("您有新的标签添加申请!!!请尽快处理", emailContent);
    }

    @Override
    protected Apply apply(AddTagReq addTagReq) {

        ApplyContentDto applyContentDto = new ApplyContentDto();
        applyContentDto.setApplyIntro(JsonUtils.toJsonStr(addTagReq));

        Apply apply = new Apply();
        apply.setApplyId(CcUtils.getUuId());
        apply.setUserId(SessionUtils.getUserId());
        apply.setApplySource(ApplySource.TAG);
        apply.setApplyContent(JsonUtils.toJsonStr(applyContentDto));
        apply.setApplyStatus(ApplyStatus.WAIT_APPLY);
        apply.setCreateTime(CcDateUtil.getCurrentTime());
        apply.setSysUpdateTime(new Date());

        if (insertApply(apply)) {
            return apply;
        }
        throw new BusinessException(ErrorCode.APPLY_TAG_FAILED);
    }
}
