/*
 * MIT License
 *
 * Copyright (c) 2021 yangrunkang
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

package com.upupor.web.router;

import com.upupor.service.business.aggregation.service.ContentService;
import com.upupor.service.common.CcEnum;
import com.upupor.service.dao.entity.Content;
import com.upupor.service.utils.ServletUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.Objects;

/**
 * 路由器
 * (单独提出来,使用redirect不会带切面里面的参数)
 *
 * @author YangRunkang(cruise)
 * @date 2020/11/07 21:38
 */
@Api(tags = "路由")
@RestController
@RequiredArgsConstructor
public class RouterPageJumpController {

    private final ContentService contentService;

    @ApiOperation("跳转到当前用户最新的文章详情")
    @GetMapping("/router/jump/content")
    public ModelAndView contentList() {
        ModelAndView modelAndView = new ModelAndView();

        String userId = ServletUtils.getUserId();
        Content content = contentService.latestContent(userId);

        // 文章为空默认到首页
        if (Objects.isNull(content)) {
            modelAndView.setViewName("redirect:/");
            return modelAndView;
        }

        if (CcEnum.ContentStatus.NORMAL.getStatus().equals(content.getStatus())) {
            modelAndView.setViewName("redirect:/u/" + content.getContentId());
            return modelAndView;
        } else if (CcEnum.ContentStatus.DRAFT.getStatus().equals(content.getStatus()) || CcEnum.ContentStatus.ONLY_SELF_CAN_SEE.getStatus().equals(content.getStatus())) {
            modelAndView.setViewName("redirect:/m/" + content.getContentId());
            return modelAndView;
        }

        // 当条件匹配不上,默认到首页
        modelAndView.setViewName("redirect:/");
        return modelAndView;
    }

    /**
     * 兼容之前的文章url
     *
     * @param source
     * @param contentId
     * @return
     */
    @GetMapping("/{source}/content/view/{contentId}")
    public ModelAndView historyUrlContent(@PathVariable("source") String source, @PathVariable("contentId") String contentId) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/u/" + contentId);
        return modelAndView;
    }

    /**
     * 兼容短内容
     */
    @GetMapping("/topic/{contentId}")
    public ModelAndView topicRouter(@PathVariable("contentId") String contentId) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/u/" + contentId);
        return modelAndView;
    }

    /**
     * 兼容短内容
     */
    @GetMapping("/topic/m/{contentId}")
    public ModelAndView topicManagerRouter(@PathVariable("contentId") String contentId) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/m/" + contentId);
        return modelAndView;
    }

}
