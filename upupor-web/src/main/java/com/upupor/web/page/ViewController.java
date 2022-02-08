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

package com.upupor.web.page;

import com.upupor.framework.CcConstant;
import com.upupor.service.common.BusinessException;
import com.upupor.service.common.ErrorCode;
import com.upupor.web.page.abstracts.AbstractView;
import com.upupor.web.page.footer.BusinessCooperationView;
import com.upupor.web.page.history.HistoryView;
import com.upupor.web.page.member.UserListView;
import io.swagger.annotations.Api;
import joptsimple.internal.Strings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;

/**
 * 公共的View Controller
 * @author Yang Runkang (cruise)
 * @date 2022年02月08日 23:21
 * @email: yangrunkang53@gmail.com
 */
@Slf4j
@Api(tags = "公共的View Controller")
@RestController
@RequiredArgsConstructor
public class ViewController {
    private final List<AbstractView> abstractViewList;



    @GetMapping({
            "/logout", // 登出
            "/register", // 注册
            "/forget-password", // 忘记密码
            UserListView.URL, // 列出所有用户
            "/login", // 登录
            "/unsubscribe-mail", // 退订邮件
            HistoryView.URL, // 浏览记录
            BusinessCooperationView.URL, // 商务合作
    })
    public ModelAndView one(HttpServletRequest request, Integer pageNum, Integer pageSize) {
        if (Objects.isNull(pageNum)) {
            pageNum = CcConstant.Page.NUM;
        }
        if (Objects.isNull(pageSize)) {
            pageSize = CcConstant.Page.SIZE;
        }
        // 通用业务逻辑
        String servletPath = request.getServletPath();
        for (AbstractView abstractView : abstractViewList) {
            String convertServletPath = abstractView.adapterUrlToViewName(servletPath);

            String viewName;
            // 如果不相等,就是适配完成后的,
            if (!convertServletPath.equals(servletPath)) {
                viewName = convertServletPath;
            } else {
                viewName = abstractView.viewName().replace(abstractView.prefix(), Strings.EMPTY);
            }

            if (viewName.equals(convertServletPath)) {
                return abstractView.doBusiness(pageNum, pageSize);
            }
        }
        throw new BusinessException(ErrorCode.NONE_PAGE);
    }


}
