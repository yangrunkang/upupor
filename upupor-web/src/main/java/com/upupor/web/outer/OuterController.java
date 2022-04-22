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

package com.upupor.web.outer;

import com.alibaba.druid.util.StringUtils;
import com.upupor.service.scheduled.GenerateSiteMapScheduled;
import com.upupor.framework.utils.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.upupor.framework.CcConstant.CvCache.SITE_MAP;


/**
 * 活动控制器
 *
 * @author YangRunkang(cruise)
 * @date 2020/02/05 00:13
 */
@Api(tags = "对外接口")
@RestController
@RequiredArgsConstructor
public class OuterController {
    private final GenerateSiteMapScheduled generateSiteMapScheduled;

    @ApiOperation("站点地图")
    @GetMapping(value = "/upupor-google-sitemap.xml", produces = {"application/xml; charset=UTF-8"})
    public String siteMap() {

        String s = RedisUtil.get(SITE_MAP);
        if (StringUtils.isEmpty(s)) {
            generateSiteMapScheduled.googleSitemap();
            s = RedisUtil.get(SITE_MAP);
        }


        return StringUtils.isEmpty(s) ? "生成SiteMap失败" : s;
    }

}
