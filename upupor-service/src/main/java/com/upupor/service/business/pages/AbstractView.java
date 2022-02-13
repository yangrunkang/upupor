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

package com.upupor.service.business.pages;

import com.upupor.framework.CcConstant;
import joptsimple.internal.Strings;
import org.springframework.web.servlet.ModelAndView;

import java.util.Objects;

/**
 * 抽象视图
 *
 * @author Yang Runkang (cruise)
 * @date 2022年01月28日 11:06
 * @email: yangrunkang53@gmail.com
 */
public abstract class AbstractView {

    protected ModelAndView modelAndView;
    protected Query query;

    /**
     * 视图名
     *
     * @return
     */
    public abstract String viewName();

    /**
     * 前缀
     *
     * @return
     */
    public String prefix() {
        return Strings.EMPTY;
    }

    /**
     * 适配ServletPath到View视图
     *
     * @param pageUrl
     * @return
     * @note: pageUrl可能会根据运营需求来变更, 但是视图名是不长变的, 所以需要这个适配
     */
    public String adapterUrlToViewName(String pageUrl) {
        // 默认 pageUrl 和视图命名是一致的,即不用适配
        return pageUrl;
    }

    /**
     * SEO信息
     */
    protected abstract void seoInfo();

    /**
     * 获取数据
     */
    protected void fetchData() {

    }

    /**
     * 处理分页参数
     */
    protected void specifyPage() {

    }

    /**
     * 做业务的方法
     *
     * @return
     */
    public ModelAndView doBusiness(Query query) {
        initData(query);

        // 设置viewName
        modelAndView.setViewName(viewName());

        //获取信息
        fetchData();

        // 设置seo信息
        seoInfo();

        return modelAndView;
    }

    protected Boolean isNeedSpecifyPage(){
        return Boolean.FALSE;
    }

    /**
     * 初始化
     *
     * @param query
     */
    private void initData(Query query) {
        this.query = query;
        modelAndView = new ModelAndView();

        // 优先使用个性化分页参数
        if(isNeedSpecifyPage()){
            specifyPage();
        } else {
            // 默认分页参数
            Integer pageNum = query.getPageNum();
            Integer pageSize = query.getPageSize();
            if (Objects.isNull(pageNum)) {
                query.setPageNum(CcConstant.Page.NUM);
            }
            if (Objects.isNull(pageSize)) {
                query.setPageSize(CcConstant.Page.SIZE);
            }
        }
    }

}
