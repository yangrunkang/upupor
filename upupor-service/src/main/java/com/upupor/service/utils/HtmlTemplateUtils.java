package com.upupor.service.utils;

import com.upupor.service.common.BusinessException;
import com.upupor.service.common.CcTemplateConstant;
import com.upupor.service.common.ErrorCode;
import org.springframework.util.CollectionUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.util.HashMap;
import java.util.Map;

/**
 * Html模板渲染工具类
 *
 * @author YangRunkang(cruise)
 * @date 2020/02/06 17:40
 */
public class HtmlTemplateUtils {

    private static final TemplateEngine TEMPLATE_ENGINE;

    //模板引擎初始化,写在static静态块中,保证只执行一次
    static {
        TEMPLATE_ENGINE = new TemplateEngine();
        //创建解析器
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        //设置字符集
        resolver.setCharacterEncoding("UTF-8");
        //设置后缀
        resolver.setSuffix(".html");
        //模板引擎和解析器关联
        TEMPLATE_ENGINE.setTemplateResolver(resolver);
    }

    /**
     * 使用 Thymeleaf 渲染 HTML
     *
     * @param template HTML模板
     * @param params   参数
     * @return 渲染后的HTML
     */
    public static String renderEmail(String template, Map<String, Object> params) {
        Context context = new Context();
        if (CollectionUtils.isEmpty(params)) {
            params = new HashMap<>(8);
        }
        if (!params.containsKey(CcTemplateConstant.TITLE) || !params.containsKey(CcTemplateConstant.CONTENT)) {
            throw new BusinessException(ErrorCode.EMAIL_LESS_PARAM);
        }
        context.setVariables(params);
        return TEMPLATE_ENGINE.process(template, context);
    }

    /**
     * 使用 Thymeleaf 渲染 HTML
     *
     * @param template HTML模板
     * @param params   参数
     * @return 渲染后的HTML
     */
    public static String renderGoogleSeoSiteMap(String template, Map<String, Object> params) {
        Context context = new Context();
        if (CollectionUtils.isEmpty(params)) {
            params = new HashMap<>(8);
        }
        context.setVariables(params);
        return TEMPLATE_ENGINE.process(template, context);
    }


    /**
     * 使用 Thymeleaf 渲染 HTML
     *
     * @param template HTML模板
     * @param params   参数
     * @return 渲染后的HTML
     */
    public static String renderMemberCardHtml(String template, Map<String, Object> params) {
        Context context = new Context();
        context.setVariables(params);
        return TEMPLATE_ENGINE.process(template, context);
    }

}
