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

import com.upupor.service.business.aggregation.service.MessageService;
import com.upupor.service.business.pages.AbstractView;
import com.upupor.service.business.pages.Query;
import com.upupor.service.business.pages.business.*;
import com.upupor.service.business.pages.content.ContentDetailView;
import com.upupor.service.business.pages.content.DraftContentDetailView;
import com.upupor.service.business.pages.footer.*;
import com.upupor.service.business.pages.history.HistoryView;
import com.upupor.service.business.pages.member.*;
import com.upupor.service.business.pages.radio.CreateRadioView;
import com.upupor.service.business.pages.radio.RadioDetailView;
import com.upupor.service.business.pages.radio.RadioListView;
import com.upupor.service.business.pages.radio.RecordView;
import com.upupor.service.business.pages.todo.TodoListView;
import com.upupor.service.business.pages.views.MarkdownView;
import com.upupor.service.common.BusinessException;
import com.upupor.service.common.ErrorCode;
import io.swagger.annotations.Api;
import joptsimple.internal.Strings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;


/**
 * 公共的View Controller
 *
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
    private final MessageService messageService;

    @GetMapping({
            LogoutView.URL, // 登出
            RegisterView.URL, // 注册
            ForgetPasswordView.URL, // 忘记密码
            UserListView.URL, // 列出所有用户
            LoginView.URL, // 登录
            UnSubscribeEmailView.URL, // 退订邮件
            HistoryView.URL, // 浏览记录
            BusinessCooperationView.URL, // 商务合作
            VisionView.URL, // 愿景
            ThanksView.URL, // 感谢
            BrandStoryView.URL, // 品牌故事
            AboutAdView.URL, // 关于广告
            DeveloperView.URL, // 团队
            ApplyAdView.URL, // 申请广告
            ApplyConsultantView.URL, // 咨询服务申请
            OpenSourceView.URL, // 开源
            MarkdownView.URL, // markdown教程
            PinnedView.URL, // 置顶
            IntegralRulesView.URL, // 积分规则
            LogoDesignView.URL, // logo设计
            FeedbackView.URL, // 反馈
            TagContentView.URL, // 标签
            SearchView.URL, // 搜索
            DailyPoints.URL, // 每日签到
            ApplyTagView.URL, // 标签申请
            TodoListView.URL, // 待办
            ContentDetailView.URL, // 内容详情-公开
            DraftContentDetailView.URL, // 内容详情-非公开
            RadioListView.URL, // 电台列表
            CreateRadioView.URL, // 创建电台
            RecordView.URL, // 在线录制电台
            RadioDetailView.URL, // 电台详情
    })
    public ModelAndView one(HttpServletRequest request,
                            Integer pageNum,
                            Integer pageSize,
                            // 标签名
                            @PathVariable(value = "tagName", required = false) String tagName,
                            // 检索
                            String keyword,
                            // 内容Id
                            @PathVariable(value = "contentId",required = false) String contentId,
                            // 消息Id
                            String msgId,
                            // 电台id
                            @PathVariable(value = "radioId",required = false) String radioId
    ) {
        if (Objects.nonNull(msgId)) {
            messageService.tagMsgRead(msgId);
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
                Query query = Query.builder()
                        .pageSize(pageSize)
                        .pageNum(pageNum)
                        .tagName(tagName)
                        .keyword(keyword)
                        .contentId(contentId)
                        .radioId(radioId)
                        .msgId(msgId)
                        .build();
                return abstractView.doBusiness(query);
            }
        }
        throw new BusinessException(ErrorCode.NONE_PAGE);
    }


}
