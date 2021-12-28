package com.upupor.service.common;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.upupor.service.common.CcConstant.OSS_URL;

/**
 * Vcr枚举
 *
 * @author: YangRunkang(cruise)
 * @created: 2019/12/24 02:24
 */
@Getter
public enum CcEnum {
    ;

    /**
     * 会员状态
     */
    @Getter
    public enum MemberStatus {
        /**
         * 正常
         */
        NORMAL(0, "正常"),

        /**
         * 禁用
         */
        FORBIDDEN(1, "禁用"),

        /**
         * 删除
         */
        DELETE(2, "删除"),
        ;
        private final Integer status;
        private final String name;

        MemberStatus(Integer status, String name) {
            this.status = status;
            this.name = name;
        }
    }

    /**
     * 用户是否是管理员
     */
    @Getter
    public enum MemberIsAdmin {
        /**
         * 非管理员
         */
        NORMAL(0, "非管理员"),

        /**
         * 管理员
         */
        ADMIN(1, "管理员"),

        ;
        private final Integer status;
        private final String name;

        MemberIsAdmin(Integer status, String name) {
            this.status = status;
            this.name = name;
        }
    }

    /**
     * 评论状态
     */
    @Getter
    public enum CommentStatus {
        /**
         * 正常
         */
        NORMAL(0, "正常"),

        /**
         * 删除
         */
        DELETE(1, "删除"),

        ;
        private final Integer status;
        private final String name;

        CommentStatus(Integer status, String name) {
            this.status = status;
            this.name = name;
        }
    }

    /**
     * 原创类型 0-无 1-非原创 2-原创
     */
    @Getter
    public enum OriginType {
        /**
         * 无
         */
        NONE(0, "无"),

        /**
         * 非原创
         */
        NONE_ORIGIN(1, "非原创"),

        /**
         * 原创
         */
        ORIGIN(2, "原创"),

        ;
        private final Integer type;
        private final String name;

        OriginType(Integer status, String name) {
            this.type = status;
            this.name = name;
        }
    }


    /**
     * 评论采纳数
     */
    @Getter
    public enum CommentAgree {
        /**
         * 正常
         */
        NONE(0, "无"),

        /**
         * 已被采纳
         */
        AGREE(1, "已被采纳"),

        /**
         * 中立
         */
        MIDDLE(2, "中立"),

        /**
         * 反对
         */
        DIS_AGREE(3, "反对"),

        ;
        private final Integer agree;
        private final String name;

        CommentAgree(Integer agree, String name) {
            this.agree = agree;
            this.name = name;
        }
    }

    /**
     * Note: 如果有新增,记得这个地方需要修改: com.upupor.web.controller.CommentsController#add(com.upupor.service.dto.req.AddCommentReq)
     */
    @Getter
    public enum ContentType {


        /**
         * 技术
         */
        TECH(1, "技术", "/tech", "发布技术文章", OSS_URL + "icons/write/write-tech.png", "创建 > 技术"),

        /**
         * 问答
         */
        QA(2, "问答", "/qa", "我要提问", OSS_URL + "icons/write/write-qa.png", "创建 > 问答"),

        /**
         * 分享
         */
        SHARE(3, "分享", "/share", "我要分享", OSS_URL + "icons/write/write-share.png", "创建 > 分享"),

        /**
         * 职场
         */
        WORKPLACE(4, "职场", "/workplace", "分享职场新鲜事", OSS_URL + "icons/write/write-work.png", "创建 > 职场"),

        /**
         * 记录
         */
        RECORD(5, "记录", "/record", "记录美好生活", OSS_URL + "icons/write/write-record.png", "创建 > 记录"),

        /**
         * 短内容
         */
        SHORT_CONTENT(6, "短内容", "/topic", "发布短内容", OSS_URL + "icons/system/topic.png", "创建 > 短内容"),

        ;
        private final Integer type;
        private final String name;
        private final String url;
        private final String webText;
        private final String icon;
        private final String tips;

        ContentType(Integer type, String name, String url, String webText, String icon, String tips) {
            this.type = type;
            this.name = name;
            this.url = url;
            this.webText = webText;
            this.icon = icon;
            this.tips = tips;
        }

        public static String getUrl(Integer contentType) {
            if (Objects.isNull(contentType)) {
                return null;
            }
            ContentType[] values = values();
            for (ContentType tmp : values) {
                if (tmp.getType().equals(contentType)) {
                    return tmp.getUrl();
                }
            }
            return null;
        }

        public static String getName(Integer contentType) {
            if (Objects.isNull(contentType)) {
                return null;
            }
            ContentType[] values = values();
            for (ContentType tmp : values) {
                if (tmp.getType().equals(contentType)) {
                    return tmp.getName();
                }
            }
            return null;
        }


        public static String getWebText(Integer contentType) {
            if (Objects.isNull(contentType)) {
                return null;
            }
            ContentType[] values = values();
            for (ContentType tmp : values) {
                if (tmp.getType().equals(contentType)) {
                    return tmp.getWebText();
                }
            }
            return null;
        }


        public static ContentType getByContentType(Integer contentType) {
            if (Objects.isNull(contentType)) {
                return null;
            }
            ContentType[] values = values();
            for (ContentType tmp : values) {
                if (tmp.getType().equals(contentType)) {
                    return tmp;
                }
            }
            return null;
        }


    }


    @Getter
    public enum ContentStatus {
        /**
         * 正常
         */
        NORMAL(0, "正常"),

        /**
         * 草稿
         */
        DRAFT(1, "草稿"),


        /**
         * 审核中
         */
        Applying(2, "审核中"),


        /**
         * 异常
         */
        EXCEPTION(3, "异常"),


        /**
         * 已删除
         */
        DELETED(4, "已删除"),


        /**
         * 回收站
         */
        RUBBISH(5, "回收站"),

        /**
         * 仅自己可见
         */
        ONLY_SELF_CAN_SEE(6, "仅自己可见"),

        ;
        private final Integer status;
        private final String name;

        ContentStatus(Integer status, String name) {
            this.status = status;
            this.name = name;
        }
    }

    /**
     * 0-公开 1-隐藏 2-删除
     */
    @Getter
    public enum RadioStatus {
        /**
         * 正常
         */
        NORMAL(0, "正常"),

        /**
         * 隐藏
         */
        HIDE(1, "隐藏"),

        /**
         * 删除
         */
        DELETED(2, "删除"),

        ;
        private final Integer status;
        private final String name;

        RadioStatus(Integer status, String name) {
            this.status = status;
            this.name = name;
        }
    }

    @Getter
    public enum ContentIsInitialStatus {
        /**
         * 一次都未发布
         */
        NOT_FIRST_PUBLISH_EVER(0, "一次都未发布"),

        /**
         * 已发布
         */
        FIRST_PUBLISHED(1, "第一次发布"),

        ;
        private final Integer status;
        private final String name;

        ContentIsInitialStatus(Integer status, String name) {
            this.status = status;
            this.name = name;
        }
    }

    /**
     * 标签类型 1-技术类 2-问答类
     */
    @Getter
    public enum TagType {
        /**
         * 技术
         */
        TECH(1, "技术"),

        /**
         * 问答
         */
        QA(2, "问答"),
        /**
         * 分享
         */
        SHARE(3, "分享"),
        /**
         * 职场
         */
        WORKPLACE(4, "职场"),
        /**
         * 记录
         */
        RECORD(5, "记录"),

        ;
        private final Integer type;
        private final String name;

        TagType(Integer type, String name) {
            this.type = type;
            this.name = name;
        }
    }


    /**
     * 内容数据项目类型
     */
    @Getter
    public enum DataItemType {
        /**
         * 查看数
         */
        VIEW_TYPE(0, "查看数"),

        /**
         * 点赞数
         */
        LIKE_TYPE(1, "点赞数"),

        /**
         * 评论数
         */
        COMMENT_TYPE(2, "评论数"),

        ;
        private final Integer type;
        private final String name;

        DataItemType(Integer type, String name) {
            this.type = type;
            this.name = name;
        }
    }

    /**
     * 埋点类型
     */
    @Getter
    public enum PointType {
        /**
         *
         */
        PAGE_REQUEST(0, "页面访问"),
        DATA_REQUEST(1, "数据访问"),
        ;
        private final Integer type;
        private final String name;

        PointType(Integer type, String name) {
            this.type = type;
            this.name = name;
        }
    }

    @Getter
    public enum DataOperationType {
        /**
         *
         */
        INCREASE(0, "增加"),
        DECREASE(1, "减少"),
        ;
        private final Integer type;
        private final String name;

        DataOperationType(Integer type, String name) {
            this.type = type;
            this.name = name;
        }
    }


    /**
     * 广告申请状态 0-待审核 1-审核中 2-已通过 3-已拒绝
     */
    @Getter
    public enum ApplyStatus {
        /**
         * 待审核
         */
        WAIT_APPLY(0, "待审核"),
        APPLYING(1, "审核中"),
        APPLY_PASS(2, "已通过"),
        APPLY_REFUSE(3, "已拒绝"),
        APPLY_TERMINATE(4, "已终止"),
        APPLY_DELETED(5, "已删除"),
        APPLY_DOCUMENT_COMMIT(6, "材料已提交"),
        ;
        private final Integer status;
        private final String name;

        ApplyStatus(Integer status, String name) {
            this.status = status;
            this.name = name;
        }
    }

    @Getter
    public enum ApplySource {
        /**
         *
         */
        CONSULTING_SERVICE(0, "咨询服务"),
        NEW_FEATURES(1, "新功能建议"),
        AD(2, "广告"),
        TAG(3, "标签"),
        ;
        private final Integer source;
        private final String name;

        ApplySource(Integer source, String name) {
            this.source = source;
            this.name = name;
        }
    }

    @Getter
    public enum CollectStatus {
        /**
         *
         */
        NORMAL(0, "正常"),
        ONLY_SELF_SEE(1, "仅自己可见"),
        DELETE(2, "删除"),
        ;
        private final Integer status;
        private final String name;

        CollectStatus(Integer source, String name) {
            this.status = source;
            this.name = name;
        }
    }

    @Getter
    public enum CollectType {
        /**
         *
         */
        CONTENT(0, "内容"),
        ;
        private final Integer type;
        private final String name;

        CollectType(Integer type, String name) {
            this.type = type;
            this.name = name;
        }
    }

    @Getter
    public enum FeedBackStatus {
        /**
         * 0-正常 1-处理中 2-已处理 3-验收中(限定3天内验收) 4-已关闭  (进度要显示在页面中)
         */
        NORMAL(0, "正常"),
        HANDLING(1, "处理中"),
        HANDLED(2, "已处理"),
        CHECKING(3, "验收中(限定3天内验收) "),
        CLOSED(4, "已关闭"),
        ;
        private final Integer status;
        private final String name;

        FeedBackStatus(Integer status, String name) {
            this.status = status;
            this.name = name;
        }
    }

    @Getter
    public enum AttentionStatus {
        /**
         *
         */
        NORMAL(0, "正常"),
        DELETED(1, "删除"),
        ;
        private final Integer status;
        private final String name;

        AttentionStatus(Integer status, String name) {
            this.status = status;
            this.name = name;
        }
    }


    @Getter
    public enum FansStatus {
        /**
         *
         */
        NORMAL(0, "正常"),
        DELETED(1, "删除"),
        ;
        private final Integer status;
        private final String name;

        FansStatus(Integer status, String name) {
            this.status = status;
            this.name = name;
        }
    }

    /**
     * 活动类型
     */
    @Getter
    public enum ActivityType {
        /**
         *
         */
        TOPIC_ACTIVITY(0, "短内容活动"),
        PROMOTE_ACTIVITY(1, "推广活动"),
        ;
        private final Integer type;
        private final String name;

        ActivityType(Integer type, String name) {
            this.type = type;
            this.name = name;
        }
    }

    /**
     * 活动状态
     */
    @Getter
    public enum ActivityStatus {
        /**
         *
         */
        NORMAL(0, "正常"),
        AUDIT_ING(1, "审核中"),
        AUDIT_PASS(2, "审核通过"),
        ACTIVITY_IN(3, "活动进行中"),
        ACTIVITY_DOWN(4, "活动下架(历史活动)"),
        DELETED(5, "删除"),
        ;
        private final Integer status;
        private final String name;

        ActivityStatus(Integer status, String name) {
            this.status = status;
            this.name = name;
        }

    }

    /**
     * Banner状态
     */
    @Getter
    public enum BannerStatus {
        /**
         *
         */
        NORMAL(0, "正常"),
        DELETED(1, "下架"),
        ;
        private final Integer status;
        private final String name;

        BannerStatus(Integer status, String name) {
            this.status = status;
            this.name = name;
        }

    }


    /**
     * 评论来源
     */
    @Getter
    public enum CommentSource {

        /**
         * 技术
         */
        TECH(1, "技术"),

        /**
         * 问答
         */
        QA(2, "问答"),

        /**
         * 分享
         */
        SHARE(3, "分享"),

        /**
         * 职场
         */
        WORKPLACE(4, "职场"),

        /**
         * 记录
         */
        RECORD(5, "记录"),

        /**
         * 短内容
         */
        SHORT_CONTENT(6, "短内容"),

        /**
         * 留言
         */
        MESSAGE(7, "留言"),

        /**
         * 电台
         */
        RADIO(8, "电台"),

        ;
        private final Integer source;
        private final String name;

        CommentSource(Integer source, String name) {
            this.source = source;
            this.name = name;
        }

        public static CommentSource getBySource(Integer source) {
            for (CommentSource value : values()) {
                if (value.getSource().equals(source)) {
                    return value;
                }
            }
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }

        /**
         * 内容资源
         *
         * @return
         */
        public static List<Integer> contentSource() {
            List<Integer> commentSourceList = new ArrayList<>();
            commentSourceList.add(TECH.getSource());
            commentSourceList.add(QA.getSource());
            commentSourceList.add(SHARE.getSource());
            commentSourceList.add(WORKPLACE.getSource());
            commentSourceList.add(RECORD.getSource());
            commentSourceList.add(SHORT_CONTENT.getSource());
            return commentSourceList;
        }

    }

    /**
     * 消息状态
     */
    @Getter
    public enum MessageStatus {
        /**
         *
         */
        UN_READ(0, "未读"),
        READ(1, "已读"),
        DELETED(2, "删除"),
        ;
        private final Integer status;
        private final String name;

        MessageStatus(Integer status, String name) {
            this.status = status;
            this.name = name;
        }
    }

    /**
     * 消息状态
     */
    @Getter
    public enum MessageType {
        /**
         *
         */
        SYSTEM(0, "系统消息"),
        USER_REPLAY(1, "用户回复"),
        ;
        private final Integer type;
        private final String name;

        MessageType(Integer type, String name) {
            this.type = type;
            this.name = name;
        }
    }

    /**
     * 消息状态
     */
    @Getter
    public enum SloganType {
        /**
         * 页面的Slogan
         */
        PAGE(0, "页面"),
        ;
        private final Integer type;
        private final String name;

        SloganType(Integer type, String name) {
            this.type = type;
            this.name = name;
        }
    }

    /**
     * 标语状态
     */
    @Getter
    public enum SloganStatus {
        /**
         * 正常
         */
        NORMAL(0, "正常"),
        /**
         * 删除
         */
        DELETE(1, "删除"),
        ;
        private final Integer status;
        private final String name;

        SloganStatus(Integer status, String name) {
            this.status = status;
            this.name = name;
        }
    }

    /**
     * 积分状态
     */
    @Getter
    public enum IntegralStatus {
        /**
         *
         */
        NORMAL(0, "正常"),
        DELETE(1, "删除"),
        ;
        private final Integer status;
        private final String name;

        IntegralStatus(Integer status, String name) {
            this.status = status;
            this.name = name;
        }
    }

    /**
     * Seo状态
     */
    @Getter
    public enum SeoStatus {
        /**
         *
         */
        NORMAL(0, "正常"),
        ;
        private final Integer status;
        private final String name;

        SeoStatus(Integer status, String name) {
            this.status = status;
            this.name = name;
        }
    }

    /**
     * Seo状态
     */
    @Getter
    public enum MemberIntegralStatus {
        /**
         *
         */
        NORMAL(0, "正常"),
        DELETED(1, "删除"),
        ;
        private final Integer status;
        private final String name;

        MemberIntegralStatus(Integer status, String name) {
            this.status = status;
            this.name = name;
        }
    }

    /**
     * 置顶状态
     */
    @Getter
    public enum PinnedStatus {
        /**
         *
         */
        UN_PINNED(0, "未置顶"),
        PINNED(1, "置顶"),
        ;
        private final Integer status;
        private final String name;

        PinnedStatus(Integer status, String name) {
            this.status = status;
            this.name = name;
        }
    }

    /**
     * 上传中
     */
    @Getter
    public enum UploadStatus {
        /**
         *
         */
        UPLOADING(0, "上传中"),
        UPLOADED(1, "上传完毕"),
        ;
        private final Integer status;
        private final String name;

        UploadStatus(Integer status, String name) {
            this.status = status;
            this.name = name;
        }
    }

    /**
     * 上传中
     */
    @Getter
    public enum OpenEmail {

        /**
         *
         */
        SUBSCRIBE_EMAIL(0, "订阅"),
        UN_SUBSCRIBE_EMAIL(1, "取消订阅"),
        ;
        private final Integer status;
        private final String name;

        OpenEmail(Integer status, String name) {
            this.status = status;
            this.name = name;
        }
    }

    /**
     * 访问的内容类型
     */
    @Getter
    public enum ViewTargetType {

        /**
         * 0-内容(包含文章和电台)  1-个人主页  2-留言板
         */
        CONTENT(0, "内容"),
        PROFILE_CONTENT(1, "个人主页-文章"),
        PROFILE_MESSAGE(2, "个人主页-留言板"),
        PROFILE_RADIO(3, "个人主页-电台"),
        PROFILE_ATTENTION(4, "个人主页-关注"),
        PROFILE_FANS(5, "个人主页-粉丝"),
        RADIO(6, "电台"),
        ;
        private final Integer type;
        private final String name;

        ViewTargetType(Integer type, String name) {
            this.type = type;
            this.name = name;
        }
    }

    /**
     * 访问的内容类型
     */
    @Getter
    public enum ViewerDeleteStatus {

        /**
         * 0-内容(包含文章和电台)  1-个人主页  2-留言板
         */
        NORMAL(0, "正常"),
        DELETE(1, "删除"),

        ;
        private final Integer status;
        private final String name;

        ViewerDeleteStatus(Integer status, String name) {
            this.status = status;
            this.name = name;
        }
    }


    /**
     * 状态 0-未完成 1-已完成 2-已暂停 3-删除
     */
    @Getter
    public enum TodoStatus {
        /**
         *
         */
        UN_DO(0, "未完成"),
        DONE(1, "已完成"),
        STOP(2, "已暂停"),
        DELETE(3, "删除"),
        ;
        private final Integer status;
        private final String name;

        TodoStatus(Integer status, String name) {
            this.status = status;
            this.name = name;
        }
    }


}
