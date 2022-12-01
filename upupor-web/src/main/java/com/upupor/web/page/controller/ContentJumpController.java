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

import com.upupor.data.dao.entity.MemberConfig;
import com.upupor.data.dao.entity.Tag;
import com.upupor.data.dao.entity.enhance.MemberEnhance;
import com.upupor.data.types.ContentType;
import com.upupor.framework.BusinessException;
import com.upupor.framework.CcConstant;
import com.upupor.framework.ErrorCode;
import com.upupor.framework.utils.ServletUtils;
import com.upupor.service.aggregation.CommonAggregateService;
import com.upupor.service.base.MemberService;
import com.upupor.service.base.TagService;
import com.upupor.service.outer.req.GetCommonReq;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.upupor.framework.CcConstant.ContentView.CONTENT_LIST;
import static com.upupor.framework.CcConstant.IS_DEFAULT_CONTENT_TYPE;
import static com.upupor.framework.CcConstant.RIGHT_ARROW;


/**
 * 公共页面跳转
 *
 * @author YangRunkang(cruise)
 * @date 2020/05/14 23:45
 */
@Api(tags = "公共页面跳转")
@RestController
@RequiredArgsConstructor
public class ContentJumpController {
    private final TagService tagService;
    private final CommonAggregateService commonAggregateService;
    private final MemberService memberService;

    public static final String WEB_INDEX = "/";

    @GetMapping(value = {
            WEB_INDEX,
            "/qa", "/qa/{tagId}",
            "/topic", "/topic/{tagId}",
            "/record", "/record/{tagId}",
            "/share", "/share/{tagId}",
            "/tech", "/tech/{tagId}",
            "/workplace", "/workplace/{tagId}"
    })
    public ModelAndView contentList(Integer pageNum, Integer pageSize,
                                    @PathVariable(value = "tagId", required = false) String tagId,
                                    HttpServletRequest request) {
        String servletPath = request.getServletPath();

        // 只有跳转到首页才需要走这个逻辑
        if (servletPath.equals(WEB_INDEX)) {
            try {
                String userId = ServletUtils.getUserId();

                MemberEnhance memberEnhance = memberService.memberInfo(userId);
                MemberConfig memberConfig = memberEnhance.getMemberConfigEnhance().getMemberConfig();
                if (Objects.isNull(memberConfig)) {
                    throw new BusinessException(ErrorCode.MEMBER_CONFIG_LESS);
                }
                String defaultContentType = memberConfig.getDefaultContentType();
                if (StringUtils.isNotEmpty(defaultContentType)) {
                    ModelAndView modelAndView = new ModelAndView();
                    modelAndView.setViewName("redirect:" + defaultContentType);
                    modelAndView.addObject(IS_DEFAULT_CONTENT_TYPE, true);
                    return modelAndView;
                }
            } catch (Exception ignored) {

            }
        }

        // 默认的逻辑
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(CONTENT_LIST);
        setViewTitle(tagId, modelAndView);

        ContentType contentType = getContentIndexContentType(servletPath);
        modelAndView.addObject(commonAggregateService.index(GetCommonReq.create(tagId, pageNum, pageSize, contentType)));
        modelAndView.addObject(CcConstant.SeoKey.TITLE, ContentType.getName(contentType));
        return modelAndView;
    }


    private ContentType getContentIndexContentType(String servletPath) {
        ContentType contentType = ContentType.getByStartUrl(servletPath);
        if (Objects.isNull(contentType)) {
            contentType = ContentType.TECH;
        }
        return contentType;
    }


    private void setViewTitle(String tagId, ModelAndView mv) {
        List<Tag> tagList = tagService.listByTagIdList(Collections.singletonList(tagId));
        if (CollectionUtils.isEmpty(tagList)) {
            return;
        }

        Object title = mv.getModelMap().getAttribute(CcConstant.SeoKey.TITLE);
        if (Objects.isNull(title)) {
            return;
        }

        title = title + RIGHT_ARROW + tagList.get(0).getTagName();
        mv.addObject(CcConstant.SeoKey.TITLE, title);
        mv.addObject(CcConstant.SeoKey.DESCRIPTION, title);
    }


}
