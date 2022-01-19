package com.upupor.web.aop.view_data;

import com.upupor.framework.CcConstant;
import com.upupor.service.utils.CcUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;
import java.util.Objects;

/**
 * @author cruise
 * @createTime 2022-01-19 18:01
 */
@Service
@RequiredArgsConstructor
@Order(6)
public class SetKeyWords implements PrepareData {
    @Override
    public void prepare(ModelAndView modelAndView) {
        Map<String, Object> model = modelAndView.getModel();
        if (!CollectionUtils.isEmpty(model)) {
            Object o = model.get(CcConstant.SeoKey.DESCRIPTION);
            if (o instanceof String) {
                // 视图名称是文章详情,则不设置keywords,由文章详情自己返回
                String viewName = modelAndView.getViewName();
                if (Objects.nonNull(viewName) && viewName.equals(CcConstant.CONTENT_INDEX)) {
                    return;
                }

                String description = (String) o;
                String segmentResult = CcUtils.getSegmentResult(description);
                modelAndView.addObject(CcConstant.SeoKey.KEYWORDS, segmentResult);
            }
        }
    }

}
