package com.upupor.web.aop.view;

import com.upupor.framework.CcConstant;
import com.upupor.framework.utils.CcDateUtil;
import com.upupor.service.business.aggregation.dao.entity.Slogan;
import com.upupor.service.business.aggregation.service.SloganService;
import com.upupor.service.types.SloganStatus;
import com.upupor.service.types.SloganType;
import com.upupor.service.utils.CcUtils;
import com.upupor.service.utils.ServletUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.upupor.framework.CcConstant.*;

/**
 * 设定Slogan
 * @author cruise
 * @createTime 2022-01-19 18:01
 */
@RequiredArgsConstructor
@Service
@Order(9)
public class SetSlogan implements PrepareData {
    private final SloganService sloganService;

    @Override
    public void prepare(ViewData viewData) {
        ModelAndView modelAndView = viewData.getModelAndView();
        String servletPath = viewData.getServletPath();
        // 网页Slogan
        Slogan pageSlogan = pageSlogan(servletPath);
        modelAndView.addObject(SLOGAN, pageSlogan);
    }

    /**
     * sloganText依然生效
     *
     * @param servletPath
     * @return
     */
    private Slogan pageSlogan(String servletPath) {
        Slogan slogan = getPageSloganByPath(servletPath);
        // 统一使用自定义的样式,不在使用Slogan配置的样式
        if (Objects.nonNull(slogan)) {
            slogan.setBgStyle(getBgImg());
        } else {
            // thymeleaf html用判断,也是使用默认的
            slogan = new Slogan();
            slogan.setBgStyle(getBgImg());
        }

        return slogan;
    }

    private String getBgImg() {
        Object bgImg = ServletUtils.getSession().getAttribute(CcConstant.Session.USER_BG_IMG);
        if (Objects.isNull(bgImg)) {
            return null;
        }
        return bgImg.toString();
    }

    private Slogan getPageSloganByPath(String servletPath) {
        try {
            // 检查是否有,有则允许继续操作(类似于操作权限)
            int has = sloganService.countSloganPathByPath(servletPath);
            if (has > 0) {
                String[] split = servletPath.split(BACKSLASH);
                String path = StringUtils.join(split, DASH);
                if (StringUtils.isEmpty(path)) {
                    // 表明是首页
                    path = "/";
                }
                // 所有状态的Slogan
                List<Slogan> sloganList = sloganService.listByPath(path);
                if (CollectionUtils.isEmpty(sloganList)) {
                    // 入库
                    Slogan slogan = new Slogan();
                    slogan.setSloganId(CcUtils.getUuId());
                    slogan.setSloganName(path);
                    slogan.setSloganType(SloganType.PAGE);
                    slogan.setCreateTime(CcDateUtil.getCurrentTime());
                    slogan.setSloganStatus(SloganStatus.NORMAL);
                    slogan.setSysUpdateTime(new Date());
                    sloganService.addSlogan(slogan);
                } else {
                    Slogan slogan = sloganList.get(0);
                    if (Objects.isNull(slogan)) {
                        return null;
                    }
                    return slogan;
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }

}
