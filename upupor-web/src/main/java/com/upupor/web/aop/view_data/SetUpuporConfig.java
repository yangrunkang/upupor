package com.upupor.web.aop.view_data;

import com.upupor.framework.config.UpuporConfig;
import com.upupor.service.dto.ContentTypeData;
import com.upupor.web.UpuporWebApplication;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import static com.upupor.framework.CcConstant.*;
import static com.upupor.framework.CcConstant.ANALYZE_SWITCH;

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
    public void prepare(ModelAndView modelAndView) {
        // 切入当前环境
        modelAndView.addObject(ACTIVE_ENV, upuporConfig.getEnv());
        modelAndView.addObject(OSS_STATIC, upuporConfig.getOssStatic());
        modelAndView.addObject(STATIC_SOURCE_VERSION, UpuporWebApplication.STATIC_SOURCE_VERSION);
        modelAndView.addObject(AD_SWITCH, upuporConfig.getAdSwitch());
        modelAndView.addObject(SUPPORT_CONTENT_TYPE_LIST, new ContentTypeData().contentTypeDataList());
        modelAndView.addObject(AD_SWITCH_RIGHT, upuporConfig.getAdSwitchRight());
        modelAndView.addObject(GOOGLE_TAG_ID, upuporConfig.getGoogleTagId());
        // 谷歌广告信息
        modelAndView.addObject(GoogleAd.CLIENT_ID, upuporConfig.getGoogleAd().getDataAdClientId());
        modelAndView.addObject(GoogleAd.RIGHT_SLOT, upuporConfig.getGoogleAd().getRightSlot());
        modelAndView.addObject(GoogleAd.FEED_SLOT, upuporConfig.getGoogleAd().getFeedSlot());
        // 分析开关
        modelAndView.addObject(ANALYZE_SWITCH, upuporConfig.getAnalyzeSwitch());
    }
}
