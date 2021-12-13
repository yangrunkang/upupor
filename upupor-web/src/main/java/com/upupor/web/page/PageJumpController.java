package com.upupor.web.page;

import com.upupor.service.common.CcConstant;
import com.upupor.service.common.IntegralEnum;
import com.upupor.service.dto.page.common.ListIntegralDto;
import com.upupor.service.service.MemberService;
import com.upupor.service.service.aggregation.MemberAggregateService;
import com.upupor.service.service.aggregation.SearchAggregateService;
import com.upupor.service.service.aggregation.TagAggregateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.io.Serializable;
import java.util.Objects;

import static com.upupor.service.common.CcConstant.*;


/**
 * 首页控制器
 * <p>
 * 不能使用@RestController
 *
 * @RequiredArgsConstructor,否则就不能跳转了,@RestController
 * @RequiredArgsConstructor只返回数据字符串了
 * @author: YangRunkang(cruise)
 * @created: 2019/11/20 00:54
 */
@Api(tags = "页面跳转")
@RestController
@RequiredArgsConstructor
public class PageJumpController implements Serializable {


    private final SearchAggregateService searchAggregateService;

    private final TagAggregateService tagAggregateService;

    private final MemberAggregateService memberAggregateService;

    private final MemberService memberService;

    @ApiOperation("每日签到")
    @GetMapping("/daily-points")
    public ModelAndView dailyPoints(Integer pageNum, Integer pageSize) {
        if (Objects.isNull(pageNum)) {
            pageNum = CcConstant.Page.NUM;
        }
        if (Objects.isNull(pageSize)) {
            pageSize = CcConstant.Page.SIZE;
        }
        ModelAndView modelAndView = new ModelAndView();
        // 签到用户
        modelAndView.addObject(memberService.dailyPointsMemberList());
        modelAndView.addObject(DAILY_POINTS, Boolean.FALSE);
        try {
            modelAndView.addObject(memberAggregateService.integralRecord(IntegralEnum.DAILY_POINTS.getRuleId(), pageNum, pageSize));
        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.addObject(new ListIntegralDto());
        }
        modelAndView.setViewName(EVERY_DAILY_POINTS);
        modelAndView.addObject(SeoKey.TITLE, "每日签到");
        modelAndView.addObject(CcConstant.SeoKey.DESCRIPTION, "每日签到");
        return modelAndView;
    }

    @ApiOperation("商务合作")
    @GetMapping("/business-cooperation")
    public ModelAndView businessCooperation() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(BUSINESS_COOPERATION);
        // Seo
        modelAndView.addObject(SeoKey.TITLE, "商务合作");
        modelAndView.addObject(SeoKey.DESCRIPTION, "Upupor商务合作项目包括广告服务,咨询服务,课程推广");
        return modelAndView;
    }

    @ApiOperation("愿景")
    @GetMapping("/vision")
    public ModelAndView vision() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(VISION);

        // Seo
        modelAndView.addObject(SeoKey.TITLE, "愿景");
        modelAndView.addObject(CcConstant.SeoKey.DESCRIPTION, "愿景");

        return modelAndView;
    }

    @ApiOperation("团队")
    @GetMapping("/team")
    public ModelAndView team() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(TEAM);

        // Seo
        modelAndView.addObject(SeoKey.TITLE, "团队");
        modelAndView.addObject(CcConstant.SeoKey.DESCRIPTION, "团队");

        return modelAndView;
    }

    @ApiOperation("感谢")
    @GetMapping("/thanks")
    public ModelAndView thanks() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(THANKS);

        // Seo
        modelAndView.addObject(SeoKey.TITLE, "感谢");
        modelAndView.addObject(CcConstant.SeoKey.DESCRIPTION, "感谢");
        return modelAndView;
    }


    @ApiOperation("品牌故事")
    @GetMapping("/brand-story")
    public ModelAndView brandStory() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(BRAND_STORY);

        // Seo
        modelAndView.addObject(SeoKey.TITLE, "品牌故事");
        modelAndView.addObject(CcConstant.SeoKey.DESCRIPTION, "品牌故事");

        return modelAndView;
    }


    @ApiOperation("关于广告")
    @GetMapping("/check-info")
    public ModelAndView checkInfo() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(CHECK_INFO);

        // Seo
        modelAndView.addObject(SeoKey.TITLE, "关于广告");
        modelAndView.addObject(CcConstant.SeoKey.DESCRIPTION, "关于广告");
        return modelAndView;
    }

    @ApiOperation("站点地图")
    @GetMapping("/sitemap")
    public ModelAndView sitemap() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(SITEMAP);
        modelAndView.addObject(SeoKey.TITLE, "站点地图");
        modelAndView.addObject(CcConstant.SeoKey.DESCRIPTION, "站点地图");
        return modelAndView;
    }

    @ApiOperation("广告申请")
    @GetMapping("/apply-ad")
    public ModelAndView applyAd() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(AD_APPLY);

        // Seo
        modelAndView.addObject(SeoKey.TITLE, "广告申请");
        modelAndView.addObject(CcConstant.SeoKey.DESCRIPTION, "广告申请");
        return modelAndView;
    }

    @ApiOperation("标签申请")
    @GetMapping("/apply-tag")
    public ModelAndView applyTag() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(TAG_APPLY);
        // Seo
        modelAndView.addObject(SeoKey.TITLE, "标签申请");
        modelAndView.addObject(CcConstant.SeoKey.DESCRIPTION, "标签申请");
        return modelAndView;
    }

    @ApiOperation("咨询服务申请")
    @GetMapping("/apply-consultant")
    public ModelAndView applyConsultant() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(CONSULTANT_APPLY);

        // Seo
        modelAndView.addObject(SeoKey.TITLE, "咨询服务申请");
        modelAndView.addObject(CcConstant.SeoKey.DESCRIPTION, "咨询服务申请");
        return modelAndView;
    }


    @ApiOperation("忘记密码")
    @GetMapping("/forget-password")
    public ModelAndView forgetPassword() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(FORGET_PASSWORD);
        // Seo
        modelAndView.addObject(SeoKey.TITLE, "忘记密码");
        modelAndView.addObject(CcConstant.SeoKey.DESCRIPTION, "忘记密码");
        return modelAndView;
    }

    @ApiOperation("搜索")
    @GetMapping("/search")
    public ModelAndView search(String keyword, Integer pageNum, Integer pageSize) {
        if (Objects.isNull(pageNum)) {
            pageNum = CcConstant.Page.NUM;
        }
        if (Objects.isNull(pageSize)) {
            // 默认搜索300条
            pageSize = Page.MAX_SIZE;
        }

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(SEARCH_INDEX);
        modelAndView.addObject(searchAggregateService.index(keyword, pageNum, pageSize));

        // Seo
        modelAndView.addObject(SeoKey.TITLE, "搜索");
        modelAndView.addObject(SeoKey.KEYWORDS, "Upupor搜索:" + keyword);
        modelAndView.addObject(CcConstant.SeoKey.DESCRIPTION, "Upupor搜索:" + keyword);

        return modelAndView;
    }

    @ApiOperation("标签")
    @GetMapping("/tag/{tagName}")
    public ModelAndView tag(@PathVariable("tagName") String tagName, Integer pageNum, Integer pageSize) {

        if (Objects.isNull(pageNum)) {
            pageNum = CcConstant.Page.NUM;
        }
        if (Objects.isNull(pageSize)) {
            // 默认搜索300条
            pageSize = CcConstant.Page.SIZE;
        }

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(TAG_INDEX);
        modelAndView.addObject(tagAggregateService.index(tagName, pageNum, pageSize));

        // Seo
        modelAndView.addObject(SeoKey.TITLE, "标签-" + tagName);
        modelAndView.addObject(SeoKey.KEYWORDS, tagName);
        modelAndView.addObject(CcConstant.SeoKey.DESCRIPTION, "标签:" + tagName);

        return modelAndView;
    }

    @ApiOperation("反馈")
    @GetMapping("/feedback")
    public ModelAndView feedback() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(FEEDBACK_INDEX);
        // Seo
        modelAndView.addObject(SeoKey.TITLE, "反馈");
        modelAndView.addObject(SeoKey.KEYWORDS, "Upupor反馈,漏洞提示,网站异常");
        modelAndView.addObject(CcConstant.SeoKey.DESCRIPTION, "Upupor反馈,漏洞提示,网站异常");
        return modelAndView;
    }

    @ApiOperation("logo设计")
    @GetMapping("/logo-design")
    public ModelAndView logoDesign() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(LOGO_DESIGN);
        // Seo
        modelAndView.addObject(SeoKey.TITLE, "logo设计");
        modelAndView.addObject(CcConstant.SeoKey.DESCRIPTION, "logo设计");
        return modelAndView;
    }

    @ApiOperation("积分规则")
    @GetMapping("/integral-rules")
    public ModelAndView integralRules() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(INTEGRAL_INDEX);
        // Seo
        modelAndView.addObject(SeoKey.TITLE, "积分规则");
        modelAndView.addObject(CcConstant.SeoKey.DESCRIPTION, "积分规则");
        return modelAndView;
    }

    @ApiOperation("置顶")
    @GetMapping("/pinned")
    public ModelAndView pinned() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(PINNED_INDEX);
        // Seo
        modelAndView.addObject(SeoKey.TITLE, "置顶");
        modelAndView.addObject(CcConstant.SeoKey.DESCRIPTION, "置顶");
        return modelAndView;
    }

    @ApiOperation("Upupor访问数据分析")
    @GetMapping("/report/access")
    public ModelAndView reporterAccess() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(REPORTER_ACCESS);
        // Seo
        modelAndView.addObject(SeoKey.TITLE, "Upupor访问数据分析");
        modelAndView.addObject(CcConstant.SeoKey.DESCRIPTION, "Upupor访问数据分析");
        return modelAndView;
    }

    @ApiOperation("更新日志")
    @GetMapping("/log")
    public ModelAndView log() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(LOG_INDEX);
        // Seo
        modelAndView.addObject(SeoKey.TITLE, "更新日志");
        modelAndView.addObject(CcConstant.SeoKey.DESCRIPTION, "更新日志");
        return modelAndView;
    }

}
