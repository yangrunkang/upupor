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

import com.upupor.service.business.manage.ManageDto;
import com.upupor.service.business.manage.business.*;
import com.upupor.service.common.BusinessException;
import com.upupor.service.common.CcConstant;
import com.upupor.service.common.ErrorCode;
import com.upupor.service.dto.page.MemberIndexDto;
import com.upupor.service.utils.ServletUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.upupor.service.common.CcConstant.*;


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
public class MemberManageJumpController {
    private final ApplyCommitManage applyCommitManage;
    private final ApplyManage applyManage;
    private final AttentionManage attentionManage;
    private final CollectManage collectManage;
    private final ContentManage contentManage;
    private final DraftManage draftManage;
    private final FansManage fansManage;
    private final FeedbackManage feedbackManage;
    private final IntegraManage integraManage;
    private final MessageManage messageManage;
    private final ProfilePhotoManage profilePhotoManage;
    private final RadioManage radioManage;
    private final CommentManage commentManage;
    private final MemberManage memberManage;

    @ApiOperation("个人中心-内容管理")
    @GetMapping("/user/manage/content")
    public ModelAndView userManageContent(Integer pageNum, Integer pageSize, String searchTitle, String select) {
        if (Objects.isNull(pageNum)) {
            pageNum = Page.NUM;
        }
        if (Objects.isNull(pageSize)) {
            pageSize = Page.SIZE;
            // 如果标题不为空,默认搜索出300条数据
            if (!StringUtils.isEmpty(searchTitle)) {
                pageSize = Page.MAX_SIZE;
            }
        }
        ManageDto build = ManageDto.builder()
                .pageSize(pageSize)
                .pageNum(pageNum)
                .userId(ServletUtils.getUserId())
                .searchTitle(searchTitle)
                .select(select).build();
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(USER_MANAGE_CONTENT);
        modelAndView.addObject(contentManage.getData(build));
        modelAndView.addObject(SeoKey.TITLE, "内容管理");
        modelAndView.addObject(SeoKey.DESCRIPTION, "内容管理");
        return modelAndView;
    }

    @ApiOperation("个人中心-草稿箱")
    @GetMapping("/user/manage/draft")
    public ModelAndView userManageDraft(Integer pageNum, Integer pageSize, String searchTitle) {
        if (Objects.isNull(pageNum)) {
            pageNum = Page.NUM;
        }
        if (Objects.isNull(pageSize)) {
            pageSize = Page.SIZE;
            // 如果标题不为空,默认搜索出300条数据
            if (!StringUtils.isEmpty(searchTitle)) {
                pageSize = Page.MAX_SIZE;
            }
        }


        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(USER_MANAGE_DRAFT);
        modelAndView.addObject(draftManage.getData(ManageDto.builder()
                .pageSize(pageSize)
                .pageNum(pageNum)
                .userId(ServletUtils.getUserId())
                .searchTitle(searchTitle).build()));
        modelAndView.addObject(SeoKey.TITLE, "草稿箱");
        modelAndView.addObject(SeoKey.DESCRIPTION, "草稿箱");
        return modelAndView;
    }


    @ApiOperation("个人中心-电台管理")
    @GetMapping("/user/manage/radio")
    public ModelAndView userManageRadio(Integer pageNum, Integer pageSize, String searchTitle) {
        if (Objects.isNull(pageNum)) {
            pageNum = Page.NUM;
        }
        if (Objects.isNull(pageSize)) {
            pageSize = Page.SIZE;
        }

        ManageDto build = ManageDto.builder()
                .pageSize(pageSize)
                .pageNum(pageNum)
                .searchTitle(searchTitle)
                .userId(ServletUtils.getUserId())
                .build();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(USER_MANAGE_RADIO);
        modelAndView.addObject(radioManage.getData(build));
        modelAndView.addObject(SeoKey.TITLE, "内容管理");
        modelAndView.addObject(SeoKey.DESCRIPTION, "内容管理");
        return modelAndView;
    }

    @ApiOperation("个人中心-收藏夹")
    @GetMapping("/user/manage/collect")
    public ModelAndView userManageCollect(Integer pageNum, Integer pageSize) {
        if (Objects.isNull(pageNum)) {
            pageNum = Page.NUM;
        }
        if (Objects.isNull(pageSize)) {
            pageSize = Page.SIZE;
        }
        ManageDto build = ManageDto.builder()
                .pageSize(pageSize)
                .pageNum(pageNum)
                .userId(ServletUtils.getUserId())
                .build();
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(USER_MANAGE_COLLECTION);
        modelAndView.addObject(collectManage.getData(build));
        modelAndView.addObject(SeoKey.TITLE, "收藏夹");
        modelAndView.addObject(SeoKey.DESCRIPTION, "收藏夹");
        return modelAndView;
    }

    @ApiOperation("个人中心-评论")
    @GetMapping("/user/manage/comment")
    public ModelAndView userManageComment(Integer pageNum, Integer pageSize) {
        if (Objects.isNull(pageNum)) {
            pageNum = Page.NUM;
        }
        if (Objects.isNull(pageSize)) {
            pageSize = Page.SIZE;
        }
        ManageDto build = ManageDto.builder()
                .pageSize(pageSize)
                .pageNum(pageNum)
                .userId(ServletUtils.getUserId())
                .build();
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(USER_MANAGE_COMMENT);
        modelAndView.addObject(commentManage.getData(build));
        modelAndView.addObject(SeoKey.TITLE, "评论");
        modelAndView.addObject(SeoKey.DESCRIPTION, "评论");
        return modelAndView;
    }


    @ApiOperation("个人中心-完善用户信息")
    @GetMapping("/user/manage/edit-user-info")
    public ModelAndView userManageEditorUserInfo() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(USER_MANAGE_EDIT_USER_INFO);
        modelAndView.addObject(memberManage.initMemberInfo());
        modelAndView.addObject(SeoKey.TITLE, "完善个人信息");
        modelAndView.addObject(SeoKey.DESCRIPTION, "完善个人信息");
        return modelAndView;
    }

    @ApiOperation("个人中心-设置网站背景颜色")
    @GetMapping("/user/manage/bg-style-settings")
    public ModelAndView bgStyleSettings() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(USER_MANAGE_BG_STYLE_SETTINGS);
        modelAndView.addObject(memberManage.initMemberInfo());
        modelAndView.addObject(SeoKey.TITLE, "自定义网站背景样式");
        modelAndView.addObject(SeoKey.DESCRIPTION, "自定义网站背景样式");
        return modelAndView;
    }

    @ApiOperation("个人中心-申请管理")
    @GetMapping("/user/manage/apply")
    public ModelAndView userManageApply(Integer pageNum, Integer pageSize) {
        if (Objects.isNull(pageNum)) {
            pageNum = Page.NUM;
        }
        if (Objects.isNull(pageSize)) {
            pageSize = Page.SIZE;
        }

        ManageDto build = ManageDto.builder()
                .pageSize(pageSize)
                .pageNum(pageNum)
                .userId(ServletUtils.getUserId())
                .build();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(EDIT_USER_MANAGE_APPLY);
        modelAndView.addObject(applyManage.getData(build));
        modelAndView.addObject(SeoKey.TITLE, "申请管理");
        modelAndView.addObject(SeoKey.DESCRIPTION, "申请管理");
        return modelAndView;
    }

    @ApiOperation("个人中心-申请管理-提交申请")
    @GetMapping("/user/manage/apply/commit/{applyId}")
    public ModelAndView userManageApplyCommit(@PathVariable String applyId) {
        ManageDto build = ManageDto.builder()
                .applyId(applyId)
                .userId(ServletUtils.getUserId())
                .build();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(EDIT_USER_MANAGE_APPLY_COMMIT);
        modelAndView.addObject(applyCommitManage.getData(build));
        modelAndView.addObject(SeoKey.TITLE, "提交材料");
        modelAndView.addObject(SeoKey.DESCRIPTION, "提交材料");
        return modelAndView;
    }

    @ApiOperation("个人中心-反馈信息管理")
    @GetMapping("/user/manage/feedback")
    public ModelAndView userManageFeedback() {
        ManageDto build = ManageDto.builder()
                .userId(ServletUtils.getUserId())
                .build();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(EDIT_USER_MANAGE_FEEDBACK);
        modelAndView.addObject(feedbackManage.getData(build));
        modelAndView.addObject(SeoKey.TITLE, "反馈管理");
        modelAndView.addObject(SeoKey.DESCRIPTION, "反馈管理");
        return modelAndView;
    }

    @ApiOperation("个人中心-个人消息/{status}")
    @GetMapping("/user/manage/message/{status}")
    public ModelAndView userManageMessage(@PathVariable("status") String status, Integer pageNum, Integer pageSize) {
        if (Objects.isNull(pageNum)) {
            pageNum = Page.NUM;
        }
        if (Objects.isNull(pageSize)) {
            pageSize = Page.SIZE;
        }
        ManageDto build = ManageDto.builder()
                .pageSize(pageSize)
                .pageNum(pageNum)
                .userId(ServletUtils.getUserId())
                .messageStatus(status)
                .build();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(EDIT_USER_MANAGE_MESSAGE);
        modelAndView.addObject(messageManage.getData(build));
        modelAndView.addObject(SeoKey.TITLE, "个人消息");
        modelAndView.addObject(SeoKey.DESCRIPTION, "个人消息");
        return modelAndView;
    }

    @ApiOperation("个人中心-上传头像")
    @GetMapping("/user/manage/upload-profile-photo")
    public ModelAndView userManageProfilePhoto() {
        ManageDto build = ManageDto.builder()
                .userId(ServletUtils.getUserId())
                .build();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(USER_MANAGE_UPLOAD_PROFILE_PHOTO);
        modelAndView.addObject(profilePhotoManage.getData(build));
        modelAndView.addObject(SeoKey.TITLE, "上传头像");
        modelAndView.addObject(SeoKey.DESCRIPTION, "上传头像");
        return modelAndView;
    }

    @ApiOperation("关注数和粉丝数")
    @GetMapping("/user/manage/pay-attention/num/{source}")
    public ModelAndView payAttentionNum(@PathVariable("source") String source, Integer pageNum, Integer pageSize) {
        if (Objects.isNull(pageNum)) {
            pageNum = Page.NUM;
        }
        if (Objects.isNull(pageSize)) {
            pageSize = Page.SIZE;
        }

        List<String> sourceList = Arrays.asList(CcConstant.USER_FANS, CcConstant.USER_ATTENTIONS);
        if (!sourceList.contains(source)) {
            throw new BusinessException(ErrorCode.PATH_ERROR);
        }
        ManageDto build = ManageDto.builder()
                .pageSize(pageSize)
                .userId(ServletUtils.getUserId())
                .pageNum(pageNum)
                .build();
        ModelAndView modelAndView = new ModelAndView();

        if (source.equals(USER_ATTENTIONS)) {
            modelAndView.addObject(attentionManage.getData(build));
            modelAndView.setViewName(USER_MANAGE_ATTENTION);
            modelAndView.addObject(SeoKey.TITLE, "您的关注");
            modelAndView.addObject(SeoKey.DESCRIPTION, "您的关注");
        } else if (source.equals(USER_FANS)) {
            modelAndView.addObject(fansManage.getData(build));
            modelAndView.setViewName(USER_MANAGE_FAN);
            modelAndView.addObject(SeoKey.TITLE, "您的粉丝");
            modelAndView.addObject(SeoKey.DESCRIPTION, "您的粉丝");
        }
        return modelAndView;
    }

    @ApiOperation("查看积分记录")
    @GetMapping("/user/manage/integral-record")
    public ModelAndView integralRecord(Integer pageNum, Integer pageSize) {
        if (Objects.isNull(pageNum)) {
            pageNum = Page.NUM;
        }
        if (Objects.isNull(pageSize)) {
            pageSize = Page.SIZE;
        }
        ManageDto build = ManageDto.builder()
                .pageSize(pageSize)
                .pageNum(pageNum)
                .userId(ServletUtils.getUserId())
                .build();

        ModelAndView modelAndView = new ModelAndView();
        MemberIndexDto memberIndexDto = integraManage.getData(build);
        modelAndView.addObject(memberIndexDto);
        modelAndView.setViewName(USER_MANAGE_INTEGRAL);
        modelAndView.addObject(SeoKey.TITLE, "积分记录");
        modelAndView.addObject(SeoKey.DESCRIPTION, "积分记录");
        return modelAndView;
    }


}
