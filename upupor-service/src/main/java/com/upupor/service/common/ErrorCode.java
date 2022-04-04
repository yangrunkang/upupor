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

package com.upupor.service.common;

/**
 * 错误码
 *
 * @author: YangRunkang(cruise)
 * @created: 2019/12/20 03:02
 */
public enum ErrorCode {

    // 常规异常 10-99
    PARAM_VALIDATE_ERROR(10, "参数验证错误"),
    PARAM_ERROR(11, "参数错误"),
    PARAM_ERROR_USER_ID(12, "参数错误，用户id为空"),
    SYSTEM_ERROR(13, "系统异常,请在页面底部反馈入口进行反馈"),
    GET_SESSION_FAILED(14, "系统异常,Session获取失败"),
    USE_EDITOR_WITHOUT_PERMISSION(15, "非法路径,未经系统允许进入编辑器页面"),
    CRATE_DIR_FAIL(16, "创建目录失败"),
    CONTAINS_ILLEGAL_CHARACTER(17, "包含非法字符"),
    PATH_WRONG(18, "路径错误,可能服务器已将该目录删除,请联系管理员\n或者,您输入的地址有误,请检查"),
    BAN_EDIT_OTHERS_CONTENT(19, "该文章不属于您,禁止编辑"),
    ILLEGAL_PATH_TO_VIEW_CONTENT(20, "通过非法路径浏览文章内容,您已被拦截"),
    NOT_BELONG_SPECIFIC_CATEGORY(21, "该文章不属于任何一种类型"),
    YOU_HAVE_NO_PERMISSION(22, "您好,您无权限进行操作"),
    EMAIL_EMPTY(23, "邮箱为空"),
    DATA_EXCEPTION(24, "数据异常"),
    LESS_CONFIG(25, "配置内容缺少"),
    ILLEGAL_FILE_SUFFIX(26, "非法文件后缀"),
    PATH_ERROR(27, "路径错误"),
    SEND_EMAIL_ERROR(28, "发送邮件错误"),
    SYSTEM_INIT_ERROR_WITHOUT_ANY_TAGS(29, "系统未初始化,无任何标签"),
    UPLOAD_ERROR(30, "上传失败"),
    CONTENT_NOT_BELONG_TO_YOU(31, "文章不属于您"),
    RADIO_NOT_BELONG_TO_YOU(32, "该音频不属于您"),
    WRONG_EMAIL(34, "请输入正确的邮箱"),
    NOT_VIEW(35, "严重错误,非视图页面"),
    SUBMIT_REPEAT(36, "重复提交"),
    NO_CONSTRUCTOR_ERROR(37, "禁止使用无参构造,统一入口"),


    // 业务异常 100-300
    REST_CONTROLLER_ERROR(100, "Rest请求控制层出现其他异常"),
    MEMBER_NOT_EXISTS(101, "用户不存在"),
    MEMBER_ALREADY_EXISTS(102, "用户已存在"),
    AD_APPLY_NOT_EXISTS(103, "广告不存在"),
    COMMENT_NOT_EXISTS(104, "评论不存在"),
    CONTENT_NOT_EXISTS(105, "<strong>文章不存在</strong> <br/>可能有以下原因: <br/>1.文章已经删除 <br/>2.文章被设置为草稿或者仅作者本人可见"),
    DATA_MISSING(106, "数据缺失"),
    AD_APPLY_LEAST_CONTACT(107, "请留下至少一种联系方式"),
    COMMENT_FAILED(108, "评论失败"),
    LOGIN_FAILED(109, "登录失败"),
    REGISTER_FAILED(110, "注册失败"),
    TWICE_DIFFERENCE_PASSWORD(111, "两次密码不相等"),
    WITHOUT_USER_PLEASE_TO_REGISTER(112, "用户未注册,不能登录,请先注册"),
    PASSWORD_ERROR(113, "用户登录失败,密码错误"),
    INIT_DATA_ERROR(114, "初始化数据失败"),
    USER_NOT_LOGIN(115, "请登录"),
    SOURCE_NOT_EXISTS(116, "资源不存在"),
    STORED_FILE_FAILED(117, "存储文件失败,请重试"),
    FILE_NOT_EXISTS(118, "文件不存在"),
    QA_WITHOUT_DEFAULT_TAGS(119, "问答页面无默认标签"),
    SHARE_WITHOUT_DEFAULT_TAGS(120, "分享页面无默认标签"),
    USER_STATUS_EXCEPTION(121, "用户状态异常"),
    EDIT_COMMENT_FAILURE(122, "编辑评论失败"),
    NOT_EXISTS_APPLY(123, "申请不存在"),
    WRONG_RE_PASSWORD(124, "两次密码输入不一致"),
    RESET_PASSWORD_FAILURE(125, "重置密码失败"),
    YOU_EMAIL_HAS_NOT_REGISTERED(126, "您输入的邮箱还未在本站注册过"),
    ARTICLE_DELETED(127, "文章已被删除,不存在这篇文章"),
    ARTICLE_NOT_BELONG_TO_YOU(128, "您好,您所访问的文章并不属于您"),
    ALREADY_COLLECTED(129, "您已经收藏了该文章"),
    COLLECT_CONTENT_ERROR(130, "收藏内容出现异常"),
    FORBIDDEN_ATTENTION_SELF(131, "不能关注自己"),
    ATTENTION_REPEATED(132, "您已经关注过该作者"),
    OBJECT_NOT_EXISTS(133, "你操作的对象不存在"),
    TOPIC_NOT_EXISTS(134, "话题不存在"),
    FORBIDDEN_COLLECT_SELF_CONTENT(135, "不能收藏自己的文章"),
    EMAIL_LESS_PARAM(136, "邮件缺少参数,请检查"),
    VERIFY_CODE_TIMEOUT(137, "验证码输入超时,请重写发送新的验证码并及时输入"),
    VERIFY_CODE_ERROR(138, "验证码输入错误,请检查"),
    COMMENT_SOURCE_NULL(139, "评论来源异常"),
    COMMENT_TARGET_NOT_EXISTS(140, "评论对象不存在"),
    INTEGRAL_NOT_ENOUGH(141, "积分不够"),
    GOOGLE_SEO_SITE_MAP_ERROR(142, "Google 站点地图获取异常"),
    ALREADY_GET_DAILY_POINTS(143, "您已领取今日积分"),
    RULE_ID_NULL(144, "ruleId为空"),
    UPLOAD_MEMBER_INFO_ERROR(145, "更新用户个人信息失败"),
    REDIS_KEY_NOT_EXISTS(146, "Redis Key 为空"),
    ALREADY_CLICK_LIKE(147, "您已经点过赞,不能重复点赞"),
    ADD_INTEGRAL_FAILED(148, "添加积分数据失败"),
    CONTENT_STATUS_NOT_NORMAL(149, "文章状态不正常"),
    USER_INFO_LESS(150, "用户信息缺少"),
    FORBIDDEN_LIKE_SELF_CONTENT(151, "不能给自己点赞"),
    FORBIDDEN_SET_PINNED_CONTENT_STATUS_NOT_NORMAL(152, "置顶文章状态不能设置为非正常状态"),
    USER_NAME_ERROR(153, "用户名不允许包含\"test(大小写)\" 及 \"测试\" 相关字样"),
    NEXT_PUBLIC_TIME(154, "不允许在短时间内发文多次,下一次可发文的时间节点为:\n"),
    EXISTS_IS_OPENING_EDITOR(155, "<strong>您有正在编辑的内容,请优先处理.</strong> <br/><br/>有以下2种处理方式: <br/>1.将正在编辑中的内容完成 <br/>2.关闭编辑器页面"),
    YOU_DROPPED_CACHE_CONTENT(156, "您已丢弃未完成的内容. 当前操作属于非常规操作."),
    UPLOAD_RADIO_ERROR(157, "发布音频失败"),
    RADIO_NOT_EXISTS(158, "音频不存在"),
    RADIO_NOT_EXITS_IN_DB(159, "音频文件不存在系统中"),
    ALI_EMAIL_DONE_CLOSED_FORGET_PASSWORD(160, "阿里邮件服务出现异常,忘记密码功能暂时关闭"),
    USER_NAME_CAN_NOT_EMPTY(161, "用户名不能为空"),
    EDIT_MEMBER_FAILED(162, "用户信息编辑失败"),
    USER_NOT_ADMIN(163, "您不是管理员,禁止访问"),
    SELECTED_VIA_NOT_EXISTS(163, "您选中的头像不存在"),
    TODO_NOT_EXIST(164, "待办事项不存在"),
    TODO_NOT_BELONG_TO_YOU(165, "待办事项不属于你"),
    TODO_STATUS_ERROR(166, "待办事项状态异常"),
    TODO_ALREADY_DELETE(167, "该待办已清除"),
    COLLECT_VIA_NOT_EXISTS(168, "收藏不存在"),
    MEMBER_CONFIG_LESS(169, "缺少用户配置"),
    FIRST_CANCEL_PINNED(170, "文章置顶中,请优先取消指定"),
    CSS_BG_CONFIG_MORE(171, "用户自定义背景只能有一个"),
    SETTING_BG_FAILED(172, "背景设置失败"),
    SETTING_DEFAULT_CONTENT_TYPE_FAILED(173, "设置喜爱的文章类型失败"),
    KEYWORDS_EMPTY(174, "请输入关键字"),
    NOT_EXISTS_ABSTRACT_EDITOR_IMPLEMENTS(175, "编辑器操作抽象未找到相关实现"),
    WITHOUT_OPERATION_ID(176, "未找到操作ID"),
    LUCENE_FLUSH_SOURCE_404(177, "刷新Lucene时未获取到资源"),


    //占位 兜底异常
    NONE_PAGE(-998, "页面不存在"),
    UNKNOWN_EXCEPTION(-999, "未知异常,请联系并协助技术部排查问题"),
    ;

    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 错误信息
     */
    private final String message;

    ErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public BusinessException getException() {
        return new BusinessException(this);
    }

    public String getFormatMessage() {
        return String.format("【%d】 %s", code, message);
    }

}
