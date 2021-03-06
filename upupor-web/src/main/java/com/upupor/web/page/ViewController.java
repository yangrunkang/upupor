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

import com.upupor.framework.BusinessException;
import com.upupor.framework.ErrorCode;
import com.upupor.service.business.pages.AbstractView;
import com.upupor.service.business.pages.Query;
import com.upupor.service.business.pages.business.*;
import com.upupor.service.business.pages.content.*;
import com.upupor.service.business.pages.footer.*;
import com.upupor.service.business.pages.history.HistoryView;
import com.upupor.service.business.pages.member.*;
import com.upupor.service.business.pages.ourhome.Earth;
import com.upupor.service.business.pages.radio.CreateRadioView;
import com.upupor.service.business.pages.radio.RadioDetailView;
import com.upupor.service.business.pages.radio.RadioListView;
import com.upupor.service.business.pages.radio.RecordView;
import com.upupor.service.business.pages.todo.TodoListView;
import com.upupor.service.business.pages.views.MarkdownView;
import com.upupor.service.data.service.MessageService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;


/**
 * ?????????View Controller
 *
 * @author Yang Runkang (cruise)
 * @date 2022???02???08??? 23:21
 * @email: yangrunkang53@gmail.com
 */
@Slf4j
@Api(tags = "?????????View Controller")
@RestController
@RequiredArgsConstructor
public class ViewController {
    private final List<AbstractView> abstractViewList;
    private final MessageService messageService;

    @GetMapping({
            LogoutView.URL, // ??????
            RegisterView.URL, // ??????
            ForgetPasswordView.URL, // ????????????
            UserListView.URL, // ??????????????????
            LoginView.URL, // ??????
            UnSubscribeEmailView.URL, // ????????????
            HistoryView.URL, // ????????????
            BusinessCooperationView.URL, // ????????????
            VisionView.URL, // ??????
            ThanksView.URL, // ??????
            BrandStoryView.URL, // ????????????
            AboutAdView.URL, // ????????????
            DeveloperView.URL, // ??????
            ApplyAdView.URL, // ????????????
            ApplyConsultantView.URL, // ??????????????????
            OpenSourceView.URL, // ??????
            MarkdownView.URL, // markdown??????
            PinnedView.URL, // ??????
            IntegralRulesView.URL, // ????????????
            LogoDesignView.URL, // logo??????
            FeedbackView.URL, // ??????
            TagContentView.URL, // ??????
            SearchView.URL, // ??????
            DailyPoints.URL, // ????????????
            ApplyTagView.URL, // ????????????
            TodoListView.URL, // ??????
            ContentDetailView.URL, // ????????????-??????
            DraftContentDetailView.URL, // ????????????-?????????
            RadioListView.URL, // ????????????
            CreateRadioView.URL, // ????????????
            RecordView.URL, // ??????????????????
            RadioDetailView.URL, // ????????????
            AllContentView.URL, // ????????????-??????
            RecentlyEditedContentView.URL, // ???????????????-??????
            NewContentView.URL, // ?????????-??????
            Earth.URL, // ????????????????????
    })
    public ModelAndView one(HttpServletRequest request,
                            Integer pageNum,
                            Integer pageSize,
                            // ?????????
                            @PathVariable(value = "tagName", required = false) String tagName,
                            // ??????
                            String keyword,
                            // ??????Id
                            @PathVariable(value = "contentId",required = false) String contentId,
                            // ??????Id
                            String msgId,
                            // ??????id
                            @PathVariable(value = "radioId",required = false) String radioId
    ) {
        if (Objects.nonNull(msgId)) {
            messageService.tagMsgRead(msgId);
        }
        // ??????????????????
        String servletPath = request.getServletPath();
        for (AbstractView abstractView : abstractViewList) {
            String convertServletPath = abstractView.adapterUrlToViewName(servletPath);

            String viewName;
            // ???????????????,????????????????????????,
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
