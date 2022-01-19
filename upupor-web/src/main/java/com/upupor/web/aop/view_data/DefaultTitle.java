package com.upupor.web.aop.view_data;

import com.upupor.framework.CcConstant;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import java.util.Objects;

/**
 * 默认标题
 * @author cruise
 * @createTime 2022-01-19 18:01
 */
@Service
@Order(1)
public class DefaultTitle implements PrepareData{
    @Override
    public void prepare(ModelAndView modelAndView) {
        // 将维度消息数显示在title
        Object title = modelAndView.getModelMap().getAttribute(CcConstant.SeoKey.TITLE);
        if (Objects.isNull(title)) {
            modelAndView.addObject(CcConstant.SeoKey.TITLE, "Upupor让每个人享受分享");
        }
    }
}
