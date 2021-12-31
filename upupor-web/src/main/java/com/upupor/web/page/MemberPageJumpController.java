/*
 * MIT License
 *
 * Copyright (c) 2021 yangrunkang
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

import com.upupor.service.business.aggregation.MemberAggregateService;
import com.upupor.service.business.aggregation.service.CommentService;
import com.upupor.service.business.aggregation.service.MemberService;
import com.upupor.service.business.aggregation.service.MessageService;
import com.upupor.service.business.profile.service.ProfileAggregateService;
import com.upupor.service.common.CcConstant;
import com.upupor.service.dto.page.MemberIndexDto;
import com.upupor.service.types.ViewTargetType;
import com.upupor.service.utils.PageUtils;
import com.upupor.service.utils.ServletUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.Objects;

import static com.upupor.service.common.CcConstant.*;
import static com.upupor.service.common.CcConstant.Page.SIZE_COMMENT;


/**
 * 用户页面跳转控制器
 *
 * @author YangRunkang(cruise)
 * @date 2020/01/19 12:16
 */
@Slf4j
@Api(tags = "用户页面跳转控制器")
@RestController
@RequiredArgsConstructor
public class MemberPageJumpController {

    private final MemberAggregateService memberAggregateService;
    private final ProfileAggregateService profileAggregateService;
    private final MessageService messageService;
    private final CommentService commentService;
    private final MemberService memberService;


    @ApiOperation("列出所有用户")
    @GetMapping("/list-user")
    public ModelAndView userList(Integer pageNum, Integer pageSize) {
        if (Objects.isNull(pageNum)) {
            pageNum = CcConstant.Page.NUM;
        }
        if (Objects.isNull(pageSize)) {
            pageSize = CcConstant.Page.SIZE;
        }

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(USER_LIST);
        modelAndView.addObject(memberAggregateService.userList(pageNum, pageSize));
        modelAndView.addObject(SeoKey.TITLE, "所有用户");
        modelAndView.addObject(SeoKey.DESCRIPTION, "所有用户");
        return modelAndView;
    }


    @ApiOperation("登录")
    @GetMapping("/login")
    public ModelAndView login() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(USER_LOGIN);

        modelAndView.addObject(SeoKey.TITLE, "登录");
        modelAndView.addObject(SeoKey.DESCRIPTION, "登录");
        return modelAndView;
    }

    @ApiOperation("注册")
    @GetMapping("/register")
    public ModelAndView register() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(USER_REGISTER);

        // Seo
        modelAndView.addObject(SeoKey.TITLE, "注册");
        modelAndView.addObject(CcConstant.SeoKey.DESCRIPTION, "注册");
        return modelAndView;
    }

    @ApiOperation("退出登录")
    @GetMapping("/logout")
    public ModelAndView logout() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(USER_LOGOUT);

        // Seo
        modelAndView.addObject(SeoKey.TITLE, "退出登录");
        modelAndView.addObject(CcConstant.SeoKey.DESCRIPTION, "退出登录");
        return modelAndView;
    }

    @ApiOperation("作者主页-文章")
    @GetMapping("/profile/{userId}")
    public ModelAndView index(@PathVariable("userId") String userId, Integer pageNum, Integer pageSize, String msgId) {
        if (Objects.isNull(pageNum)) {
            pageNum = CcConstant.Page.NUM;
        }
        if (Objects.isNull(pageSize)) {
            pageSize = CcConstant.Page.SIZE;
        }

        if (Objects.nonNull(msgId)) {
            messageService.tagMsgRead(msgId);
        }

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(AUTHOR_PROFILE);
        MemberIndexDto memberIndexDto = profileAggregateService.index(userId, pageNum, pageSize, ViewTargetType.PROFILE_CONTENT);
        modelAndView.addObject(memberIndexDto);
        modelAndView.addObject(SeoKey.TITLE, memberIndexDto.getMember().getUserName());
        modelAndView.addObject(SeoKey.DESCRIPTION, memberIndexDto.getMember().getMemberExtend().getIntroduce());
        return modelAndView;
    }

    @ApiOperation("作者主页-关注")
    @GetMapping("/profile/{userId}/attention")
    public ModelAndView attetion(@PathVariable("userId") String userId, Integer pageNum, Integer pageSize, String msgId) {
        if (Objects.isNull(pageNum)) {
            pageNum = CcConstant.Page.NUM;
        }
        if (Objects.isNull(pageSize)) {
            pageSize = CcConstant.Page.SIZE;
        }

        if (Objects.nonNull(msgId)) {
            messageService.tagMsgRead(msgId);
        }

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(AUTHOR_ATTENTION);
        MemberIndexDto memberIndexDto = profileAggregateService.index(userId, pageNum, pageSize, ViewTargetType.PROFILE_ATTENTION);
        modelAndView.addObject(memberIndexDto);
        modelAndView.addObject(SeoKey.TITLE, memberIndexDto.getMember().getUserName() + "的关注");
        modelAndView.addObject(SeoKey.DESCRIPTION, memberIndexDto.getMember().getMemberExtend().getIntroduce());
        return modelAndView;
    }


    @ApiOperation("作者主页-粉丝")
    @GetMapping("/profile/{userId}/fans")
    public ModelAndView fans(@PathVariable("userId") String userId, Integer pageNum, Integer pageSize, String msgId) {
        if (Objects.isNull(pageNum)) {
            pageNum = CcConstant.Page.NUM;
        }
        if (Objects.isNull(pageSize)) {
            pageSize = CcConstant.Page.SIZE;
        }

        if (Objects.nonNull(msgId)) {
            messageService.tagMsgRead(msgId);
        }

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(AUTHOR_FANS);
        MemberIndexDto memberIndexDto = profileAggregateService.index(userId, pageNum, pageSize, ViewTargetType.PROFILE_FANS);
        modelAndView.addObject(memberIndexDto);
        modelAndView.addObject(SeoKey.TITLE, memberIndexDto.getMember().getUserName() + "的粉丝");
        modelAndView.addObject(SeoKey.DESCRIPTION, memberIndexDto.getMember().getMemberExtend().getIntroduce());
        return modelAndView;
    }

    @ApiOperation("作者主页-电台")
    @GetMapping("/profile/{userId}/radio")
    public ModelAndView indexRadio(@PathVariable("userId") String userId, Integer pageNum, Integer pageSize, String msgId) {
        if (Objects.isNull(pageNum)) {
            pageNum = CcConstant.Page.NUM;
        }
        if (Objects.isNull(pageSize)) {
            pageSize = CcConstant.Page.SIZE;
        }

        if (Objects.nonNull(msgId)) {
            messageService.tagMsgRead(msgId);
        }

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(AUTHOR_RADIO);
        MemberIndexDto memberIndexDto = profileAggregateService.index(userId, pageNum, pageSize, ViewTargetType.PROFILE_RADIO);
        modelAndView.addObject(memberIndexDto);
        modelAndView.addObject(SeoKey.TITLE, memberIndexDto.getMember().getUserName());
        modelAndView.addObject(SeoKey.DESCRIPTION, memberIndexDto.getMember().getMemberExtend().getIntroduce());
        return modelAndView;
    }

    @ApiOperation("作者主页-留言板")
    @GetMapping("/profile-message/{userId}")
    public ModelAndView profileMessage(@PathVariable("userId") String userId, Integer pageNum, Integer pageSize, String msgId) {
        if (Objects.isNull(pageNum)) {
            // 获取最新的评论
            Integer count = commentService.countByTargetId(userId);
            pageNum = PageUtils.calcMaxPage(count, SIZE_COMMENT);
        }

        if (Objects.isNull(pageSize)) {
            pageSize = CcConstant.Page.SIZE_COMMENT;
        }

        if (Objects.nonNull(msgId)) {
            messageService.tagMsgRead(msgId);
        }

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(AUTHOR_MESSAGE);
        MemberIndexDto memberIndexDto = profileAggregateService.index(userId, pageNum, pageSize, ViewTargetType.PROFILE_MESSAGE);
        modelAndView.addObject(memberIndexDto);
        modelAndView.addObject(SeoKey.TITLE, memberIndexDto.getMember().getUserName() + "留言板");
        modelAndView.addObject(SeoKey.DESCRIPTION, memberIndexDto.getMember().getMemberExtend().getIntroduce());
        return modelAndView;
    }

    @ApiOperation("退订邮件")
    @GetMapping("unsubscribe-mail")
    @ResponseBody
    public ModelAndView unSubscribeMail() {
        String result = "result";
        ModelAndView modelAndView = new ModelAndView();
        String userId = ServletUtils.getUserId();
        Boolean success = memberService.unSubscribeMail(userId);
        modelAndView.addObject(result, success);
        modelAndView.setViewName(UNSUBSCRIBE_MAIL);
        modelAndView.addObject(SeoKey.TITLE, "退订邮件通知");
        modelAndView.addObject(SeoKey.DESCRIPTION, "退订,邮件");
        return modelAndView;
    }


}
