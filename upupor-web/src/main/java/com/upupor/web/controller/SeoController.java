package com.upupor.web.controller;

import com.upupor.service.common.CcResponse;
import com.upupor.service.scheduled.GenerateSiteMapScheduled;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * SeoController
 *
 * @author YangRunkang(cruise)
 * @date 2020/03/12 05:22
 */
@Api(tags = "SEO")
@RestController
@RequiredArgsConstructor
public class SeoController {

    private final GenerateSiteMapScheduled generateSiteMapScheduled;


    @GetMapping("/refresh-google-sitemap")
    public CcResponse googleSeo() {
        CcResponse c = new CcResponse();
        generateSiteMapScheduled.googleSitemap();
        c.setData("Google站点地图已经刷新");
        return c;
    }

}
