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

package com.upupor.web.aop.view;

import com.upupor.framework.config.UpuporConfig;
import com.upupor.service.data.dao.entity.Member;
import com.upupor.service.data.dao.entity.MemberConfig;
import com.upupor.service.data.service.MemberService;
import com.upupor.framework.BusinessException;
import com.upupor.framework.ErrorCode;
import com.upupor.service.dto.ContentTypeData;
import com.upupor.service.utils.ServletUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static com.upupor.framework.CcConstant.SUPPORT_CONTENT_TYPE_LIST;
import static com.upupor.framework.CcConstant.USER_SETTING_DEFAULT_CONTENT;
import static com.upupor.service.dto.ContentTypeData.FIRST;

/**
 * 这里要优先展示用户喜爱的文章类型
 *
 * @author cruise
 * @createTime 2022-02-12 16:11
 */
@RequiredArgsConstructor
@Service
@Order(10)
public class SetUserDefaultContentType implements PrepareData {
    private final MemberService memberService;
    private final UpuporConfig upuporConfig;

    @Override
    public void prepare(ViewData viewData) {
        ModelAndView modelAndView = viewData.getModelAndView();

        List<ContentTypeData> contentTypeData = new ContentTypeData().contentTypeDataList(upuporConfig.getOss().getFileHost());

        try {
            String userId = ServletUtils.getUserId();
            Member member = memberService.memberInfo(userId);
            MemberConfig memberConfig = member.getMemberConfig();
            if (Objects.isNull(memberConfig)) {
                throw new BusinessException(ErrorCode.MEMBER_CONFIG_LESS);
            }
            String defaultContentType = memberConfig.getDefaultContentType();
            if (StringUtils.isNotEmpty(defaultContentType)) {
                contentTypeData.forEach(contentType -> {
                    if (contentType.getUrl().equals(defaultContentType)) {
                        contentType.setSorted(FIRST);
                    }
                });
                modelAndView.addObject(USER_SETTING_DEFAULT_CONTENT, Boolean.TRUE);
            } else {
                // 为空,用户就没有设置自己喜好的内容
                modelAndView.addObject(USER_SETTING_DEFAULT_CONTENT, Boolean.FALSE);
            }
        } catch (Exception ignored) {
        }

        contentTypeData.sort(Comparator.comparingInt(ContentTypeData::getSorted));
        modelAndView.addObject(SUPPORT_CONTENT_TYPE_LIST, contentTypeData);
    }


}
