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

package com.upupor.web.page;

import com.upupor.framework.CcConstant;
import com.upupor.service.business.aggregation.CommonAggregateService;
import com.upupor.service.business.aggregation.dao.entity.Tag;
import com.upupor.service.business.aggregation.service.TagService;
import com.upupor.service.outer.req.GetCommonReq;
import com.upupor.service.types.ContentType;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.upupor.framework.CcConstant.CONTENT_LIST;
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
public class CommonPageJumpController {
    private final TagService tagService;
    private final CommonAggregateService commonAggregateService;

    @GetMapping(value = {
            "/",
            "/qa", "/qa/{tagId}", "/qa/{tagId}/{tagInId}",
            "/topic",
            "/record", "/record/{tagId}",
            "/share", "/share/{tagId}", "/share/{tagId}/{tagInId}",
            "/tech", "/tech/{tagId}", "/tech/{tagId}/{tagInId}",
            "/workplace", "/workplace/{tagId}"
    })
    public ModelAndView qa(Integer pageNum, Integer pageSize,
                           @PathVariable(value = "tagId", required = false) String tagId,
                           @PathVariable(value = "tagInId", required = false) String tagInId,
                           HttpServletRequest request) {
        String servletPath = request.getServletPath();

        ContentType contentType = ContentType.getByStartUrl(servletPath);
        if (Objects.isNull(contentType)) {
            contentType = ContentType.TECH;
        }

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject(commonAggregateService.index(GetCommonReq.create(tagId, tagInId, pageNum, pageSize, contentType)));
        modelAndView.addObject(CcConstant.SeoKey.TITLE, ContentType.getName(contentType));
        modelAndView.setViewName(CONTENT_LIST);
        // 绑定一级文章标题
        setViewTitle(tagId, modelAndView);

        return modelAndView;
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
