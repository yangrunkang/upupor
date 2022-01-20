package com.upupor.web.aop.view;

import com.upupor.framework.CcConstant;
import com.upupor.service.utils.RedisUtil;
import com.upupor.service.utils.ServletUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import static com.upupor.framework.CcConstant.CONTENT_IS_DONE;

/**
 * @author cruise
 * @createTime 2022-01-19 18:01
 */
@Service
@RequiredArgsConstructor
@Order(7)
public class CheckContentIsDone implements PrepareData{
    @Override
    public void prepare(ViewData viewData) {
        ModelAndView modelAndView = viewData.getModelAndView();
        try {
            String cacheContentKey = CcConstant.CvCache.CONTENT_CACHE_KEY + ServletUtils.getUserId();
            String content = RedisUtil.get(cacheContentKey);
            if (!StringUtils.isEmpty(content)) {
                modelAndView.addObject(CONTENT_IS_DONE, Boolean.TRUE);
            }
        } catch (Exception e) {
            modelAndView.addObject(CONTENT_IS_DONE, Boolean.FALSE);
        }
    }
}
