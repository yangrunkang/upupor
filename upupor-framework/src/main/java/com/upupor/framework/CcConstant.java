/*
 * <!--
 *   ~ MIT License
 *   ~
 *   ~ Copyright (c) 2021-2022 yangrunkang
 *   ~
 *   ~ Author: yangrunkang
 *   ~ Email: yangrunkang53@gmail.com
 *   ~
 *   ~ Permission is hereby granted, free of charge, to any person obtaining a copy
 *   ~ of this software and associated documentation files (the "Software"), to deal
 *   ~ in the Software without restriction, including without limitation the rights
 *   ~ to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *   ~ copies of the Software, and to permit persons to whom the Software is
 *   ~ furnished to do so, subject to the following conditions:
 *   ~
 *   ~ The above copyright notice and this permission notice shall be included in all
 *   ~ copies or substantial portions of the Software.
 *   ~
 *   ~ THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   ~ IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *   ~ FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *   ~ AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *   ~ LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *   ~ OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *   ~ SOFTWARE.
 *   -->
 */

package com.upupor.framework;

import com.upupor.framework.config.UpuporConfig;
import com.upupor.framework.utils.SpringContextUtils;

/**
 * Cv常量
 *
 * @author: YangRunkang(cruise)
 * @created: 2019/12/23 02:19
 */
public final class CcConstant {
    /**
     * 用户默认头像
     */
    public static final String DEFAULT_VIA = "/system/profile-photo.png";

    /**
     * 业务标识
     */
    public static final String UPUPOR_EMAIL = "yangrunkang53@gmail.com";
    /**
     * 未读消息计数
     */
    public static final String UNREAD_MSG_COUNT = "unReadMegCount";
    /**
     * 草稿箱计数
     */
    public static final String DRAFT_COUNT = "draftCount";
    /**
     * 用户的操作需要提示
     */
    public static final String TIPS_OPERATION_SHOULD_LOGIN = "loginTips";
    /**
     * 是否有默认内容类型
     */
    public static final String IS_DEFAULT_CONTENT_TYPE = "isDefaultContentType";
    /**
     * 当前环境
     */
    public static final String ACTIVE_ENV = "activeEnv";
    /**
     * 是否开启广告
     */
    public static final String AD_SWITCH = "adSwitch";
    public static final String AD_SWITCH_RIGHT = "adSwitchRight";
    /**
     * 是否开启分析
     */
    public static final String ANALYZE_SWITCH = "analyzeSwitch";
    /**
     * OSS访问地址
     */
    public static final String OSS_STATIC = "ossStatic";
    /**
     * 每日积分,签到积分
     */
    public static final String DAILY_POINTS = "dailyPoints";
    /**
     * 响应时间
     */
    public static final String RESPONSE_TIME = "responseTime";

    /**
     * 根据标签获取对应的数目
     */
    public static final String CV_TAG_LIST = "cvTagList";
    /**
     * 跳过是否订阅邮件配置校验
     */
    public static final String NOTIFY_ADMIN = "NOTIFY_ADMIN";

    /**
     * 系统支持的内容类型
     */
    public static final String SUPPORT_CONTENT_TYPE_LIST = "contentTypeList";

    /**
     * 用户是否设置内容喜好
     */
    public static final String USER_SETTING_DEFAULT_CONTENT = "isSettingDefaultContent";

    /**
     * 静态资源版本
     */
    public static final String STATIC_SOURCE_VERSION = "staticSourceVersion";

    /**
     * Google分析ID
     */
    public static final String GOOGLE_TAG_ID = "googleTagId";
    public static final String GOOGLE_GA_4 = "googleGA4";

    /**
     * 系统常量
     */
    public static final String GLOBAL_EXCEPTION = "cvException";
    public static final String EMPTY_STR = "";
    public static final String COMMA = ",";
    public static final char COMMA_CHAR = ',';
    public static final String COMMA_ZH = "，";
    public static final String BLANK = "_";
    public static final String ONE_DOTS = ".";
    public static final String DE = "的";
    public static final String BREAK_LINE = "\n";
    public static final String HTML_BREAK_LINE = "<br />";
    public static final String BACKSLASH = "/";
    public static final String DASH = "_";
    public static final String RIGHT_ARROW = " > ";
    public static final String TWO_COLONS = "::";
    public static final String AT = "@";
    /**
     * 0-关闭 1-开启
     */
    public static final String CV_OFF = "0";
    public static final String CV_ON = "1";

    public static final class UserView {
        public static final String BASE_PATH = "user";
        public static final String USER_LOGOUT = BASE_PATH + "/logout";
        public static final String USER_REGISTER = BASE_PATH + "/register";
        public static final String FORGET_PASSWORD = BASE_PATH + "/forget-password";
        public static final String USER_LIST = BASE_PATH + "/list";
        public static final String USER_LOGIN = BASE_PATH + "/login";
        public static final String UNSUBSCRIBE_MAIL = BASE_PATH + "/unsubscribe-mail";
    }


    public static final String SEARCH_INDEX = "search/index";
    public static final String TAG_INDEX = "tag/index";
    public static final String EDITOR = "editor/editor";

    public static final String EDIT_USER_MANAGE_ADMIN = "user/admin/admin";
    public static final String EDIT_USER_MANAGE_CONTENT = "user/admin/content";
    public static final String FEEDBACK_INDEX = "feedback/index";
    public static final String INTEGRAL_INDEX = "integral/index";
    public static final String PINNED_INDEX = "foot/pinned";
    public static final String RADIO_STATION_LIST = "radio-station/list";
    public static final String RADIO_STATION_CREATE = "radio-station/create";
    public static final String RADIO_STATION_RECORD = "radio-station/record";
    public static final String RADIO_STATION_INDEX = "radio-station/index";
    public static final String TODO_INDEX = "todo/index";
    public static final String VIEW_HISTORY = "view/index";
    public static final String OPEN = "foot/opensource";
    public static final String MARKDOWN = "components/markdown";
    public static final String OUR_HOME = "components/our-home";

    public static final class ContentView {
        // 文章列表
        public static final String CONTENT_LIST = "content/list";
        // 全部文章 、 最近编辑过的
        public static final String CONTENT_TYPE = "content/type";
        // 文章详情
        public static final String CONTENT_INDEX = "content/index";
    }

    /**
     * 运营活动
     */
    public static final String EVERY_DAILY_POINTS = "operate/daily-points";
    /**
     * foot底部
     */
    public static final String BUSINESS_COOPERATION = "foot/business-cooperation";
    public static final String VISION = "foot/vision";
    public static final String DEVELOPER = "foot/developer";
    public static final String THANKS = "foot/thanks";
    public static final String BRAND_STORY = "foot/brand-story";
    public static final String CHECK_INFO = "foot/about-ad";
    public static final String LOGO_DESIGN = "foot/logo-design";
    /**
     * 商务合作
     */
    public static final String AD_APPLY = "business/ad-apply";
    public static final String TAG_APPLY = "business/tag-apply";
    public static final String CONSULTANT_APPLY = "business/consultant-apply";
    /**
     * 异常页面
     */
    public static final String PAGE_404 = "error/404";
    public static final String PAGE_500 = "error/500";
    public static final String URL_404 = "/error/404";
    public static final String URL_500 = "/error/500";

    public static final class UserManageView {
        // 基础Path
        public static final String BASE_PATH = "user/manage/";

        // 各个页面的View名
        public static final String USER_MANAGE_COLLECTION = BASE_PATH + "collect";
        public static final String USER_MANAGE_CONTENT = BASE_PATH + "content";
        public static final String USER_MANAGE_DRAFT = BASE_PATH + "draft";
        public static final String USER_MANAGE_RADIO = BASE_PATH + "radio";
        public static final String USER_MANAGE_CONTENT_SETTINGS = BASE_PATH + "content-settings";
        public static final String USER_MANAGE_EDIT_USER_INFO = BASE_PATH + "edit-user-info";
        public static final String USER_MANAGE_BG_STYLE_SETTINGS = BASE_PATH + "bg-style-settings";
        public static final String USER_MANAGE_UPLOAD_PROFILE_PHOTO = BASE_PATH + "upload-profile-photo";
        public static final String USER_MANAGE_DEFAULT_CONTENT_TYPE = BASE_PATH + "default-content-setting";
        public static final String USER_MANAGE_ATTENTION = BASE_PATH + "attention";
        public static final String USER_MANAGE_FAN = BASE_PATH + "fan";
        public static final String USER_MANAGE_INTEGRAL = BASE_PATH + "integral-record";
        public static final String USER_MANAGE_APPLY_COMMIT = BASE_PATH + "apply-commit";
        public static final String USER_MANAGE_FEEDBACK = BASE_PATH + "feedback";
        public static final String USER_MANAGE_MESSAGE = BASE_PATH + "message";
        public static final String USER_MANAGE_APPLY = BASE_PATH + "apply";
    }

    public static final class ProfileView {

        // 基础Path
        public static final String BASE_PATH = "profile/";

        public static final String PROFILE_CONTENT = BASE_PATH + "content";
        public static final String PROFILE_ATTENTION = BASE_PATH + "attention";
        public static final String PROFILE_FANS = BASE_PATH + "fans";
        public static final String PROFILE_RADIO = BASE_PATH + "radio";
        public static final String PROFILE_MESSAGE = BASE_PATH + "message";

    }


    /**
     * SEO Key
     */
    public static final class SeoKey {
        public static final String TITLE = "seo_title";
        public static final String DESCRIPTION = "seo_description";
        public static final String KEYWORDS = "seo_keywords";
        public static final String BUSINESS_TITLE = "businessTitle";
    }

    /**
     * SEO Value
     */
    public static final class SeoValue {
        public static String TITLE = "Upupor 让每个人享受分享";
        public static String KEYWORDS = "upupor,upupor.com,分享,朋友,线下,技术,问答,分享,职场,记录";
    }

    /**
     * 页面
     */
    public static final class Page {
        /**
         * 起始页码
         */
        public static final Integer NUM = 1;
        /**
         * 每页条数
         */
        public static final Integer SIZE = 15;
        public static final Integer SIZE_HALF = 7;
        public static final Integer SIZE_COMMENT = 30;
        public static final Integer SIZE_TEN = 10;
        /**
         * 最大条数
         */
        public static final Integer MAX_SIZE = 300;
    }

    /**
     * Session相关
     */
    public static final class Session {
        /**
         * 用户id
         */
        public static final String USER_ID = "cv_user_id";
        /**
         * 用户头像
         */
        public static final String USER_VIA = "cv_user_via";

        /**
         * 用户姓名
         */
        public static final String USER_NAME = "cv_user_name";

        /**
         * 用户背景图
         */
        public static final String USER_BG_IMG = "cv_user_bg_img";

        /**
         * 位置用户Id
         */
        public static final String UNKNOWN_USER_ID = "unknown_user_id";

        /**
         * 是否是管理员
         */
        public static final String IS_ADMIN = "is_admin";

        /**
         * 用户是否很长时间未更换头像
         */
        public static final String LONG_TIME_UN_UPDATE_PROFILE_PHOTO = "long_time_un_update_profile_photo";

    }

    /**
     * 消息模板
     */
    public static final class MsgTemplate {
        private static final String WEBSITE = SpringContextUtils.getBean(UpuporConfig.class).getWebsite();
        /**
         * 个人主页
         * userId,userName
         */
        public static final String PROFILE_INNER_MSG = "<a style='cursor: pointer;position: relative;text-decoration: none;font-weight: bold;color: #000000;' href = '/profile/%s/content?source=inner-message&msgId=%s'> %s </a>";
        public static final String PROFILE_INTEGRAL = "<a style='cursor: pointer;position: relative;text-decoration: none;font-weight: bold;color: #000000;' href = '/profile/%s/content?source=integral&msgId=%s'> %s </a>";
        public static final String PROFILE_EMAIL = "<a style='cursor: pointer;position: relative;text-decoration: none;font-weight: bold;color: #000000;' href = '" + WEBSITE + "/profile/%s/content?source=email&msgId=%s'> %s </a>";

        /**
         * 文章
         * contentId,contentTitle
         */
        public static final String CONTENT_INNER_MSG = "<a style='cursor: pointer;position: relative;text-decoration: none;font-weight: bold;color: #000000;' href = '/u/%s?source=inner-message&msgId=%s'>%s</a>";
        public static final String CONTENT_INTEGRAL = "<a style='cursor: pointer;position: relative;text-decoration: none;font-weight: bold;color: #000000;' href = '/u/%s?source=integral&msgId=%s'>%s</a>";
        public static final String CONTENT_EMAIL = "<a style='cursor: pointer;position: relative;text-decoration: none;font-weight: bold;color: #000000;' href = '" + WEBSITE + "/u/%s?source=email&msgId=%s'>%s</a>";

        /**
         * 文章
         * contentId,contentTitle
         */
        public static final String RADIO_INNER_MSG = "<a style='cursor: pointer;position: relative;text-decoration: none;font-weight: bold;color: #000000;' href = '/r/%s?source=inner-message&msgId=%s'>%s</a>";
        public static final String RADIO_INTEGRAL = "<a style='cursor: pointer;position: relative;text-decoration: none;font-weight: bold;color: #000000;' href = '/r/%s?source=integral&msgId=%s'>%s</a>";
        public static final String RADIO_EMAIL = "<a style='cursor: pointer;position: relative;text-decoration: none;font-weight: bold;color: #000000;' href = '" + WEBSITE + "/r/%s?source=email&msgId=%s'>%s</a>";

        /**
         * 留言
         */
        public static final String MESSAGE_EMAIL = "<a style='cursor: pointer;position: relative;text-decoration: none;font-weight: bold;color: #000000;' href = '" + WEBSITE + "/profile/%s/message?source=email&msgId=%s'>%s</a>";
        public static final String MESSAGE_INTEGRAL = "<a style='cursor: pointer;position: relative;text-decoration: none;font-weight: bold;color: #000000;' href = '/profile/%s/message?source=integral&msgId=%s'>%s</a>";


    }

    public static final class Time {
        // 用户只要活跃一次,维持活跃状态的时间
        public static final long MEMBER_ACTIVE_TIME = 60 * 60L;
        // 文章只要编辑过,维持更新标识的时间
        public static final long CONTENT_UPDATE_TIME = 24 * 60 * 60L;
        // 最新的新文章 7天内都算
        public static final long NEW_CONTENT_TIME = 7 * 24 * 60 * 60L;
    }


    /**
     * 缓存常量
     */
    public static final class GoogleAd {
        public static final String FEED_AD = "google-ad-feed";
        public static final String CLIENT_ID = "googleClient";
        public static final String RIGHT_SLOT = "googleRightSlot";
        public static final String FEED_SLOT = "googleFeedSlot";
    }

}
