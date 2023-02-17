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

package com.upupor.web.page.controller;

import com.upupor.data.dao.entity.enhance.MemberEnhance;
import com.upupor.data.dto.page.MemberIndexDto;
import com.upupor.data.utils.PageUtils;
import com.upupor.framework.BusinessException;
import com.upupor.framework.CcConstant;
import com.upupor.framework.ErrorCode;
import com.upupor.service.base.CommentService;
import com.upupor.service.base.MessageService;
import com.upupor.service.business.profile.AbstractProfile;
import com.upupor.service.business.profile.dto.Query;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Objects;

import static com.upupor.framework.CcConstant.Page.SIZE;
import static com.upupor.framework.CcConstant.ProfileView.BASE_PATH;
import static com.upupor.framework.CcConstant.ProfileView.PROFILE_MESSAGE;
import static com.upupor.framework.CcConstant.SeoKey;

/**
 * 个人主页
 *
 * @author Yang Runkang (cruise)
 * @date 2022年01月27日 16:00
 * @email: yangrunkang53@gmail.com
 */
@Slf4j
@Api(tags = "个人主页页面跳转控制器")
@RestController
@RequiredArgsConstructor
public class MemberProfilePageJumpController {
    private final List<AbstractProfile> abstractProfileList;
    private final MessageService messageService;
    private final CommentService commentService;

    @ApiOperation("作者主页-文章")
    @GetMapping({
            "/profile/{userId}/{path}",
            "/profile/{userId}/{path}/{tagName}"
    })
    public ModelAndView profile(@PathVariable("userId") String userId,
                                @PathVariable("path") String path,
                                @PathVariable(value = "tagName", required = false) String tagName,
                                Integer pageNum, Integer pageSize,
                                String msgId) {
        boolean isPageNumNull = Objects.isNull(pageNum);
        if (isPageNumNull) {
            pageNum = CcConstant.Page.NUM;
        }

        if (Objects.isNull(pageSize)) {
            pageSize = CcConstant.Page.SIZE;
        }

        // 标记消息已读
        if (Objects.nonNull(msgId)) {
            messageService.tagMsgRead(msgId);
        }

        for (AbstractProfile abstractProfile : abstractProfileList) {
            if (abstractProfile.viewName().replace(BASE_PATH, Strings.EMPTY).equals(path)) {
                ModelAndView modelAndView = new ModelAndView();
                modelAndView.setViewName(abstractProfile.viewName());
                // 如果是留言板,直接跳转到最新的一页
                if (PROFILE_MESSAGE.replace(BASE_PATH, Strings.EMPTY).equals(path)) {
                    pageNum = isPageNumNull ? PageUtils.calcMaxPage(commentService.countByTargetId(userId), SIZE) : pageNum;
                    pageSize = CcConstant.Page.SIZE;
                }

                Query build = Query.builder()
                        .pageNum(pageNum)
                        .pageSize(pageSize)
                        .userId(userId)
                        .tagName(tagName)
                        .build();
                MemberIndexDto memberIndexDto = abstractProfile.getBusinessData(build);
                MemberEnhance memberEnhance = memberIndexDto.getMemberEnhance();
                modelAndView.addObject(memberIndexDto);
                modelAndView.addObject(SeoKey.TITLE, memberEnhance.getMember().getUserName());
                modelAndView.addObject(SeoKey.DESCRIPTION, memberEnhance.getMemberExtendEnhance().getMemberExtend().getIntroduce());
                return modelAndView;
            }
        }
        throw new BusinessException(ErrorCode.NONE_PAGE);
    }
}
