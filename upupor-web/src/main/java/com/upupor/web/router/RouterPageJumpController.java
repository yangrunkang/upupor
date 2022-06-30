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

package com.upupor.web.router;

import com.upupor.service.business.pages.footer.AboutAdView;
import com.upupor.service.business.pages.footer.DeveloperView;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

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

    private final static String REDIRECT = "redirect:";
    private final static String INDEX = REDIRECT + "/";
    private final static String PUBLIC_CONTENT = REDIRECT + "/u/";
    private final static String DRAFT_CONTENT = REDIRECT + "/m/";
    private final static String PROFILE = REDIRECT + "/profile/";

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
        modelAndView.setViewName(PUBLIC_CONTENT + contentId);
        return modelAndView;
    }

    /**
     * 留言板路由兼容
     */
    @GetMapping("/profile-message/{userId}")
    public ModelAndView profileMessage(@PathVariable("userId") String userId) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(PROFILE + userId + "/message");
        return modelAndView;
    }

    /**
     * 个人主页文章
     */
    @GetMapping("/profile/{userId}")
    public ModelAndView profileContent(@PathVariable("userId") String userId) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(PROFILE + userId + "/content");
        return modelAndView;
    }

    /**
     * 团队链接转开发者
     */
    @GetMapping("/team")
    public ModelAndView developer() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(REDIRECT + DeveloperView.URL);
        return modelAndView;
    }

    /**
     * 关于广告 /check-info更改为/about-ad
     */
    @GetMapping("/check-info")
    public ModelAndView aboutAd() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(REDIRECT + AboutAdView.URL);
        return modelAndView;
    }

}
