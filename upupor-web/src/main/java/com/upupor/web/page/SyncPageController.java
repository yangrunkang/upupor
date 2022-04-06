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

import com.upupor.service.business.aggregation.dao.entity.Content;
import com.upupor.service.business.aggregation.service.ContentService;
import com.upupor.service.dto.page.ContentIndexDto;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.Objects;

import static com.upupor.framework.CcConstant.ContentView.CONTENT_INDEX;
import static com.upupor.framework.CcConstant.TWO_COLONS;


/**
 * @author YangRunkang(cruise)
 * @date 2020/10/25 00:38
 */
@Api(tags = "异步刷新控制器")
@RestController
@RequiredArgsConstructor
public class SyncPageController {

    private final ContentService contentService;


    @GetMapping("sync/content/like")
    public ModelAndView work(ModelAndView model, String contentId) {
        Content content = contentService.getContentByContentIdNoStatus(contentId);
        if (Objects.isNull(content)) {
            return null;
        }

        contentService.bindLikesMember(content);

        ContentIndexDto contentIndexDto = new ContentIndexDto();
        contentIndexDto.setContent(content);


        model.addObject(contentIndexDto);
        model.setViewName(CONTENT_INDEX + TWO_COLONS + "sync_like_area");

        return model;
    }

}
