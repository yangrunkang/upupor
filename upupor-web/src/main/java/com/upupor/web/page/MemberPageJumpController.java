package com.upupor.web.page;

import com.upupor.service.common.BusinessException;
import com.upupor.service.common.CcConstant;
import com.upupor.service.common.CcEnum;
import com.upupor.service.common.ErrorCode;
import com.upupor.service.dto.page.MemberIndexDto;
import com.upupor.service.service.CommentService;
import com.upupor.service.service.MemberService;
import com.upupor.service.service.MessageService;
import com.upupor.service.service.ViewerService;
import com.upupor.service.service.aggregation.MemberAggregateService;
import com.upupor.service.service.aggregation.MemberListAggregateService;
import com.upupor.service.service.aggregation.ProfileAggregateService;
import com.upupor.service.service.aggregation.RadioAggregateService;
import com.upupor.service.utils.PageUtils;
import com.upupor.service.utils.ServletUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.List;
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

    private final MemberListAggregateService memberListAggregateService;

    private final ProfileAggregateService profileAggregateService;

    private final MessageService messageService;

    private final RadioAggregateService radioAggregateService;

    private final CommentService commentService;

    private final MemberService memberService;

    private final ViewerService viewerService;


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
        modelAndView.addObject(memberListAggregateService.userList(pageNum, pageSize));
        modelAndView.addObject(SeoKey.TITLE, "所有用户");
        modelAndView.addObject(SeoKey.DESCRIPTION, "所有用户");
        return modelAndView;
    }

    @ApiOperation("个人中心-内容管理")
    @GetMapping("/user/manage/content")
    public ModelAndView userManageContent(Integer pageNum, Integer pageSize, String searchTitle, String select) {
        if (Objects.isNull(pageNum)) {
            pageNum = CcConstant.Page.NUM;
        }
        if (Objects.isNull(pageSize)) {
            pageSize = CcConstant.Page.SIZE;
            // 如果标题不为空,默认搜索出300条数据
            if (!StringUtils.isEmpty(searchTitle)) {
                pageSize = CcConstant.Page.MAX_SIZE;
            }
        }

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(USER_MANAGE_CONTENT);
        modelAndView.addObject(memberAggregateService.manageContent(pageNum, pageSize, searchTitle, select));
        modelAndView.addObject(SeoKey.TITLE, "内容管理");
        modelAndView.addObject(SeoKey.DESCRIPTION, "内容管理");
        return modelAndView;
    }

    @ApiOperation("个人中心-草稿箱")
    @GetMapping("/user/manage/draft")
    public ModelAndView userManageDraft(Integer pageNum, Integer pageSize, String searchTitle) {
        if (Objects.isNull(pageNum)) {
            pageNum = CcConstant.Page.NUM;
        }
        if (Objects.isNull(pageSize)) {
            pageSize = CcConstant.Page.SIZE;
            // 如果标题不为空,默认搜索出300条数据
            if (!StringUtils.isEmpty(searchTitle)) {
                pageSize = CcConstant.Page.MAX_SIZE;
            }
        }

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(USER_MANAGE_DRAFT);
        modelAndView.addObject(memberAggregateService.manageDraft(pageNum, pageSize, searchTitle));
        modelAndView.addObject(SeoKey.TITLE, "草稿箱");
        modelAndView.addObject(SeoKey.DESCRIPTION, "草稿箱");
        return modelAndView;
    }


    @ApiOperation("个人中心-电台管理")
    @GetMapping("/user/manage/radio")
    public ModelAndView userManageRadio(Integer pageNum, Integer pageSize, String searchTitle) {
        if (Objects.isNull(pageNum)) {
            pageNum = CcConstant.Page.NUM;
        }
        if (Objects.isNull(pageSize)) {
            pageSize = CcConstant.Page.SIZE;
        }

        String userId = ServletUtils.getUserId();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(USER_MANAGE_RADIO);
        modelAndView.addObject(radioAggregateService.manageRadio(pageNum, pageSize, userId, searchTitle));
        modelAndView.addObject(SeoKey.TITLE, "内容管理");
        modelAndView.addObject(SeoKey.DESCRIPTION, "内容管理");
        return modelAndView;
    }

    @ApiOperation("个人中心-收藏夹")
    @GetMapping("/user/manage/collect")
    public ModelAndView userManageCollect(Integer pageNum, Integer pageSize) {
        if (Objects.isNull(pageNum)) {
            pageNum = CcConstant.Page.NUM;
        }
        if (Objects.isNull(pageSize)) {
            pageSize = CcConstant.Page.SIZE;
        }

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(USER_MANAGE_COLLECTION);
        modelAndView.addObject(memberAggregateService.manageCollect(pageNum, pageSize));
        modelAndView.addObject(SeoKey.TITLE, "收藏夹");
        modelAndView.addObject(SeoKey.DESCRIPTION, "收藏夹");
        return modelAndView;
    }

    @ApiOperation("个人中心-评论")
    @GetMapping("/user/manage/comment")
    public ModelAndView userManageComment(Integer pageNum, Integer pageSize) {
        if (Objects.isNull(pageNum)) {
            pageNum = CcConstant.Page.NUM;
        }
        if (Objects.isNull(pageSize)) {
            pageSize = CcConstant.Page.SIZE;
        }

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(USER_MANAGE_COMMENT);
        modelAndView.addObject(memberAggregateService.manageComment(pageNum, pageSize));
        modelAndView.addObject(SeoKey.TITLE, "评论");
        modelAndView.addObject(SeoKey.DESCRIPTION, "评论");
        return modelAndView;
    }


    @ApiOperation("个人中心-完善用户信息")
    @GetMapping("/user/manage/edit-user-info")
    public ModelAndView userManageEditorUserInfo() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(USER_MANAGE_EDIT_USER_INFO);
        modelAndView.addObject(memberAggregateService.getEditMemberInfo());
        modelAndView.addObject(SeoKey.TITLE, "完善个人信息");
        modelAndView.addObject(SeoKey.DESCRIPTION, "完善个人信息");
        return modelAndView;
    }

    @ApiOperation("个人中心-设置网站背景颜色")
    @GetMapping("/user/manage/bg-style-settings")
    public ModelAndView bgStyleSettings() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(USER_MANAGE_BG_STYLE_SETTINGS);
        modelAndView.addObject(memberAggregateService.getEditMemberInfo());
        modelAndView.addObject(SeoKey.TITLE, "自定义网站背景样式");
        modelAndView.addObject(SeoKey.DESCRIPTION, "自定义网站背景样式");
        return modelAndView;
    }

    @ApiOperation("个人中心-申请管理")
    @GetMapping("/user/manage/apply")
    public ModelAndView userManageApply(Integer pageNum, Integer pageSize) {
        if (Objects.isNull(pageNum)) {
            pageNum = CcConstant.Page.NUM;
        }
        if (Objects.isNull(pageSize)) {
            pageSize = CcConstant.Page.SIZE;
        }

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(EDIT_USER_MANAGE_APPLY);
        modelAndView.addObject(memberAggregateService.manageApply(pageNum, pageSize));
        modelAndView.addObject(SeoKey.TITLE, "申请管理");
        modelAndView.addObject(SeoKey.DESCRIPTION, "申请管理");
        return modelAndView;
    }

    @ApiOperation("个人中心-申请管理-提交申请")
    @GetMapping("/user/manage/apply/commit/{applyId}")
    public ModelAndView userManageApplyCommit(@PathVariable String applyId) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(EDIT_USER_MANAGE_APPLY_COMMIT);
        modelAndView.addObject(memberAggregateService.manageApplyCommit(applyId));
        modelAndView.addObject(SeoKey.TITLE, "提交材料");
        modelAndView.addObject(SeoKey.DESCRIPTION, "提交材料");
        return modelAndView;
    }

    @ApiOperation("个人中心-反馈信息管理")
    @GetMapping("/user/manage/feedback")
    public ModelAndView userManageFeedback() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(EDIT_USER_MANAGE_FEEDBACK);
        modelAndView.addObject(memberAggregateService.manageFeedback());
        modelAndView.addObject(SeoKey.TITLE, "反馈管理");
        modelAndView.addObject(SeoKey.DESCRIPTION, "反馈管理");
        return modelAndView;
    }

    @ApiOperation("个人中心-个人消息/{status}")
    @GetMapping("/user/manage/message/{status}")
    public ModelAndView userManageMessage(@PathVariable("status") String status, Integer pageNum, Integer pageSize) {
        if (Objects.isNull(pageNum)) {
            pageNum = CcConstant.Page.NUM;
        }
        if (Objects.isNull(pageSize)) {
            pageSize = CcConstant.Page.SIZE;
        }

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(EDIT_USER_MANAGE_MESSAGE);
        modelAndView.addObject(memberAggregateService.manageMessage(status, pageNum, pageSize));
        modelAndView.addObject(SeoKey.TITLE, "个人消息");
        modelAndView.addObject(SeoKey.DESCRIPTION, "个人消息");
        return modelAndView;
    }

    @ApiOperation("个人中心-上传头像")
    @GetMapping("/user/manage/upload-profile-photo")
    public ModelAndView userManageProfilePhoto() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(USER_MANAGE_UPLOAD_PROFILE_PHOTO);
        modelAndView.addObject(memberAggregateService.manageProfilePhoto());
        modelAndView.addObject(SeoKey.TITLE, "上传头像");
        modelAndView.addObject(SeoKey.DESCRIPTION, "上传头像");
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

    @ApiOperation("关注数和粉丝数")
    @GetMapping("/user/manage/pay-attention/num/{source}")
    public ModelAndView payAttentionNum(@PathVariable("source") String source, Integer pageNum, Integer pageSize) {
        if (Objects.isNull(pageNum)) {
            pageNum = CcConstant.Page.NUM;
        }
        if (Objects.isNull(pageSize)) {
            pageSize = CcConstant.Page.SIZE;
        }

        List<String> sourceList = Arrays.asList(CcConstant.USER_FANS, CcConstant.USER_ATTENTIONS);
        if (!sourceList.contains(source)) {
            throw new BusinessException(ErrorCode.PATH_ERROR);
        }

        ModelAndView modelAndView = new ModelAndView();

        if (source.equals(USER_ATTENTIONS)) {
            modelAndView.addObject(memberAggregateService.manageAttention(pageNum, pageSize));
            modelAndView.setViewName(USER_MANAGE_ATTENTION);
            modelAndView.addObject(SeoKey.TITLE, "您的关注");
            modelAndView.addObject(CcConstant.SeoKey.DESCRIPTION, "您的关注");
        } else if (source.equals(USER_FANS)) {
            modelAndView.addObject(memberAggregateService.manageFans(pageNum, pageSize));
            modelAndView.setViewName(USER_MANAGE_FAN);
            modelAndView.addObject(SeoKey.TITLE, "您的粉丝");
            modelAndView.addObject(CcConstant.SeoKey.DESCRIPTION, "您的粉丝");
        }
        return modelAndView;
    }

    @ApiOperation("查看积分记录")
    @GetMapping("/user/manage/integral-record")
    public ModelAndView integralRecord(Integer pageNum, Integer pageSize) {
        if (Objects.isNull(pageNum)) {
            pageNum = CcConstant.Page.NUM;
        }
        if (Objects.isNull(pageSize)) {
            pageSize = CcConstant.Page.SIZE;
        }

        ModelAndView modelAndView = new ModelAndView();
        MemberIndexDto memberIndexDto = memberAggregateService.manageIntegral(pageNum, pageSize);
        modelAndView.addObject(memberIndexDto);
        modelAndView.setViewName(USER_MANAGE_INTEGRAL);
        modelAndView.addObject(SeoKey.TITLE, "积分记录");
        modelAndView.addObject(CcConstant.SeoKey.DESCRIPTION, "积分记录");
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
        MemberIndexDto memberIndexDto = profileAggregateService.index(userId, pageNum, pageSize, CcEnum.ViewTargetType.PROFILE_CONTENT);
        modelAndView.addObject(memberIndexDto);
        modelAndView.addObject(SeoKey.TITLE, memberIndexDto.getMember().getUserName());
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
        MemberIndexDto memberIndexDto = profileAggregateService.index(userId, pageNum, pageSize, CcEnum.ViewTargetType.PROFILE_RADIO);
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
        MemberIndexDto memberIndexDto = profileAggregateService.index(userId, pageNum, pageSize, CcEnum.ViewTargetType.PROFILE_MESSAGE);
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
