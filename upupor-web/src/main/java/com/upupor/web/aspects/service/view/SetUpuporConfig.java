/*
 * <!--
 *   ~ MIT License
 *   ~
 *   ~ Copyright (c) 2021-2022 yangrunkang
 *   ~
 *   ~ Author: yangrunkang
 *   ~ Email: yangrunkang53@gmail.com
 *   ~
 *   ~ Permission is hereby granted, free of charge, to any person obtaining a copy
 *   ~ of this software and associated documentation files (the "Software"), to deal
 *   ~ in the Software without restriction, including without limitation the rights
 *   ~ to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *   ~ copies of the Software, and to permit persons to whom the Software is
 *   ~ furnished to do so, subject to the following conditions:
 *   ~
 *   ~ The above copyright notice and this permission notice shall be included in all
 *   ~ copies or substantial portions of the Software.
 *   ~
 *   ~ THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   ~ IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *   ~ FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *   ~ AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *   ~ LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *   ~ OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *   ~ SOFTWARE.
 *   -->
 */

package com.upupor.web.aspects.service.view;

import com.upupor.framework.config.GoogleAd;
import com.upupor.framework.config.UpuporConfig;
import com.upupor.web.UpuporWebApplication;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import static com.upupor.framework.CcConstant.*;

/**
 * @author cruise
 * @createTime 2022-01-19 18:01
 */
@RequiredArgsConstructor
@Service
@Order(5)
public class SetUpuporConfig implements PrepareData {
    private final UpuporConfig upuporConfig;

    @Override
    public void prepare(ViewData viewData) {
        ModelAndView modelAndView = viewData.getModelAndView();
        // 切入当前环境
        modelAndView.addObject(ACTIVE_ENV, upuporConfig.getEnv());
        modelAndView.addObject(OSS_STATIC, upuporConfig.getOssStaticPrefix() + upuporConfig.getBusinessStaticSource());
        modelAndView.addObject(STATIC_SOURCE_VERSION, UpuporWebApplication.STATIC_SOURCE_VERSION);
        modelAndView.addObject(AD_SWITCH, upuporConfig.getAdSwitch());
        modelAndView.addObject(AD_SWITCH_RIGHT, upuporConfig.getAdSwitchRight());
        modelAndView.addObject(GOOGLE_TAG_ID, upuporConfig.getGoogleTagId());
        modelAndView.addObject(GOOGLE_GA_4, upuporConfig.getGoogleGa4());
        // 谷歌广告信息
        GoogleAd googleAdConfig = upuporConfig.getGoogleAd();
        modelAndView.addObject(GoogleAdConstant.CLIENT_ID, googleAdConfig.getDataAdClientId());
        modelAndView.addObject(GoogleAdConstant.RIGHT_SLOT, googleAdConfig.getRightSlot());
        modelAndView.addObject(GoogleAdConstant.FEED_SLOT, googleAdConfig.getFeedSlot());
        // 分析开关
        modelAndView.addObject(ANALYZE_SWITCH, upuporConfig.getAnalyzeSwitch());
    }


}
