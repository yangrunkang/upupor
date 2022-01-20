package com.upupor.web.aop.view;

import com.alibaba.fastjson.JSON;
import com.upupor.service.scheduled.CountTagScheduled;
import com.upupor.service.utils.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Objects;

import static com.upupor.framework.CcConstant.CV_TAG_LIST;
import static com.upupor.framework.CcConstant.CvCache.TAG_COUNT;

/**
 * @author cruise
 * @createTime 2022-01-19 18:01
 */
@Service
@Order(8)
@RequiredArgsConstructor
public class TagCountFromRedis implements PrepareData{
    private final CountTagScheduled countTagScheduled;
    @Override
    public void prepare(ViewData viewData) {
        ModelAndView modelAndView = viewData.getModelAndView();
        String s = RedisUtil.get(TAG_COUNT);
        Object result = JSON.parseObject(s, Object.class);
        if (Objects.isNull(result)) {
            modelAndView.addObject(CV_TAG_LIST, new ArrayList<>());
            // 刷新下
            countTagScheduled.refreshTag();
        }

        modelAndView.addObject(CV_TAG_LIST, result);
    }
}
