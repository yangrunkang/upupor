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

CREATE TABLE  IF NOT EXISTS `ad` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `ad_id` varchar(32) NOT NULL COMMENT '广告id',
  `ad_position_id` varchar(64) DEFAULT NULL COMMENT '广告位置id(唯一标识) all-top,all-bottom,tech-right-class,tech-right-promote,tech-right-ad,qa,share,workplace',
  `ad_intro` varchar(1024) DEFAULT NULL COMMENT '广告介绍',
  `ad_url` varchar(1024) DEFAULT NULL COMMENT '广告url',
  `ad_img` varchar(1024) DEFAULT NULL COMMENT '广告图片展示Url',
  `ad_status` int(2) DEFAULT NULL COMMENT '0-正常 1-删除',
  `create_time` bigint(11) DEFAULT NULL COMMENT '创建时间',
  `sys_update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '系统更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='广告\n目前有这些广告\nall-top\nall-bottom\ntech-right-class\ntech-right-promote\ntech-right-ad\nqa\nshare\nworkplace';


CREATE TABLE  IF NOT EXISTS `apply` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `apply_id` varchar(32) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '申请id',
  `user_id` varchar(32) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '用户Id(当前网站用户申请的)',
  `apply_content` text COLLATE utf8mb4_bin COMMENT '申请内容',
  `reply_content` text COLLATE utf8mb4_bin COMMENT '申请内容回复',
  `apply_source` int(2) DEFAULT NULL COMMENT '申请来源(类型) 0-咨询服务1-新功能建议 2-广告 3-标签',
  `apply_status` int(2) DEFAULT NULL COMMENT '申请状态 0-待审核 1-审核中 2-已通过 3-已拒绝 4-已终止 5-已删除 6-材料已提交',
  `apply_user_name` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '申请者名称',
  `apply_user_phone` varchar(32) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '申请者电话',
  `apply_user_email` varchar(32) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '申请者邮件',
  `apply_user_qq` varchar(32) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '申请者QQ',
  `apply_user_wechat` varchar(32) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '申请者微信',
  `create_time` bigint(11) DEFAULT NULL COMMENT '创建时间',
  `sys_update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '系统更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC COMMENT='广告申请';

CREATE TABLE  IF NOT EXISTS `apply_document` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `apply_document_id` varchar(32) DEFAULT NULL COMMENT '申请文件id(该表id)',
  `apply_id` varchar(32) DEFAULT NULL COMMENT '审核id',
  `img_url` varchar(1024) DEFAULT NULL COMMENT '图片url',
  `upload` varchar(1024) DEFAULT NULL COMMENT '上传资料',
  `copy_writing` varchar(512) DEFAULT NULL COMMENT '文案',
  `create_time` bigint(11) DEFAULT NULL COMMENT '创建时间',
  `sys_update_time` datetime DEFAULT NULL COMMENT '系统更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='申请文件';


CREATE TABLE  IF NOT EXISTS `attention` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `attention_id` varchar(32) DEFAULT NULL COMMENT '关注id',
  `user_id` varchar(32) DEFAULT NULL COMMENT '用户id',
  `attention_user_id` varchar(32) DEFAULT NULL COMMENT '被关注的用户id',
  `attention_status` int(2) DEFAULT NULL COMMENT '关注状态 0-正常 1-删除',
  `create_time` bigint(11) DEFAULT NULL COMMENT '创建时间',
  `sys_update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '系统更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='关注表(当前用户关注其他人)';


CREATE TABLE  IF NOT EXISTS `banner` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `banner_id` varchar(32) DEFAULT NULL COMMENT 'banner主键',
  `banner_title` varchar(256) DEFAULT NULL COMMENT 'banner 标题',
  `banner_desc` varchar(256) DEFAULT NULL COMMENT 'banner 描述',
  `banner_status` int(2) DEFAULT NULL COMMENT 'banner 状态 0-正常 1-下架(删除)',
  `banner_href` varchar(512) DEFAULT NULL COMMENT 'banner 链接(要链接到系统有效的链接[为了方便推广可以自定义id不用使用系统生成的,这个自己修改DB即可],不会另开页面了)',
  `banner_pic` varchar(256) DEFAULT NULL COMMENT 'banner 图片(1220*400) (使用在线的图片剪裁,然后上传到自己的服务器,最后在DB中配置展示)',
  `create_time` bigint(11) DEFAULT NULL COMMENT '创建时间',
  `sys_update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '系统更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='页面主banner ';


CREATE TABLE  IF NOT EXISTS `buried_point_data` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `point_id` varchar(32) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '埋点id',
  `session_id` varchar(128) COLLATE utf8mb4_bin DEFAULT NULL COMMENT 'session_id',
  `user_id` varchar(128) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '用户id',
  `point_type` int(2) DEFAULT NULL COMMENT '埋点类型 0-页面访问 1-数据层访问',
  `request_method` varchar(256) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '请求方式',
  `ip` varchar(32) COLLATE utf8mb4_bin DEFAULT NULL COMMENT 'ip或服务名',
  `port` bigint(11) DEFAULT NULL COMMENT '端口',
  `servlet_path` varchar(128) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '请求地址',
  `parameters` varchar(512) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '参数',
  `create_time` bigint(11) DEFAULT NULL COMMENT '创建时间',
  `sys_update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '系统更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC COMMENT='埋点数据';


CREATE TABLE  IF NOT EXISTS `collect` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `collect_id` varchar(32) DEFAULT NULL COMMENT '收藏id',
  `user_id` varchar(32) DEFAULT NULL COMMENT '用户id',
  `collect_value` varchar(32) DEFAULT NULL COMMENT '收藏',
  `collect_type` int(2) DEFAULT NULL COMMENT '收藏类型 0-内容(收藏类型为0时,则collect_value代表内容id)',
  `status` int(2) DEFAULT NULL COMMENT '状态 0-正常 1-仅自己可见 2-删除',
  `create_time` bigint(11) DEFAULT NULL COMMENT '创建时间',
  `sys_update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '系统更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='收藏';


CREATE TABLE  IF NOT EXISTS `comment` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `user_id` varchar(32) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '用户id(关联member_id)',
  `comment_id` varchar(32) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '评论id',
  `comment_content` text COLLATE utf8mb4_bin COMMENT '评论内容',
  `target_id` varchar(32) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '被评论的目标Id',
  `comment_source` int(2) DEFAULT NULL COMMENT '评论来源 1-技术 2-问答 3-分享 4-职场 5-记录 6-短内容 7-留言板',
  `status` int(2) DEFAULT '0' COMMENT '状态 0-正常 1-删除 2-仅自己可见',
  `agree` int(2) DEFAULT '0' COMMENT '采纳 0-未被采纳(无) 1-已被采纳 2-中立 3-反对',
  `like_num` int(11) DEFAULT '0' COMMENT '点赞数(后续可以做点赞排名)',
  `create_time` bigint(11) DEFAULT NULL COMMENT '创建时间',
  `sys_update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '系统更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `index_comment_comment_id` (`comment_id`),
  KEY `index_comment_target_id` (`target_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC COMMENT='评论表';


CREATE TABLE  IF NOT EXISTS `content` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `content_id` varchar(32) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '内容Id',
  `user_id` varchar(32) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '用户id(关联member表)',
  `title` varchar(256) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '标题',
  `short_content` varchar(1024) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '简述',
  `picture` varchar(128) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '图片地址(用于列表图片的显示,如果没有就显示默认的)',
  `content_type` int(2) DEFAULT '-1' COMMENT '文章类型 1-技术 2-问答 3-分享 4-职场 5-记录 6-短内容',
  `tag_ids` varchar(256) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '标签id(多个以","隔开)',
  `edit_times` int(2) DEFAULT NULL COMMENT '编辑次数',
  `status` int(2) DEFAULT NULL COMMENT '状态 0-正常 1-草稿 2-审核中 3-异常 4-删除 5-回收站 6-仅自己可见',
  `create_time` bigint(11) DEFAULT NULL COMMENT '创建时间',
  `sys_update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '系统更新时间',
  `pinned_status` int(2) DEFAULT NULL COMMENT '置顶状态 0-未置顶 1-置顶',
  `none_origin_link` text COLLATE utf8mb4_bin COMMENT '非原创(转载链接)',
  `origin_type` int(2) DEFAULT NULL COMMENT '原创类型 0-无 1-非原创 2-原创',
  `statement_id` int(2) DEFAULT NULL COMMENT '声明id',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `index_content_content_id` (`content_id`),
  KEY `index_content_title` (`title`),
  KEY `index_content_tag_id` (`tag_ids`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC COMMENT='内容';


CREATE TABLE  IF NOT EXISTS `content_data` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `content_id` varchar(32) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '内容id',
  `view_num` int(9) DEFAULT '0' COMMENT '内容浏览数',
  `like_num` int(9) DEFAULT '0' COMMENT '点赞数',
  `comment_num` int(9) DEFAULT '0' COMMENT '评论数',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `index_content_data_id` (`content_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC COMMENT='内容数据';


CREATE TABLE  IF NOT EXISTS `content_edit_reason` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `content_id` varchar(32) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '内容id',
  `reason` text COLLATE utf8mb4_bin COMMENT '内容编辑原因',
  `create_time` bigint(11) DEFAULT NULL COMMENT '创建时间',
  `sys_update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '系统更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `index_content_edit_reason_content_id` (`content_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='内容编辑原因';


CREATE TABLE  IF NOT EXISTS `content_extend` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `content_id` varchar(32) COLLATE utf8mb4_bin DEFAULT NULL COMMENT 'content_id(关联faq表)',
  `detail_content` longtext COLLATE utf8mb4_bin COMMENT '详细内容',
  `sys_update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '系统更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `index_content_extend_content_id` (`content_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC COMMENT='内容拓展表';


CREATE TABLE  IF NOT EXISTS `fans` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `fan_id` varchar(32) DEFAULT NULL COMMENT '粉丝id',
  `user_id` varchar(32) DEFAULT NULL COMMENT '用户id',
  `fan_user_id` varchar(32) DEFAULT NULL COMMENT '被粉的用户id',
  `fan_status` int(2) DEFAULT NULL COMMENT '状态 0-正常 1-删除',
  `create_time` bigint(11) DEFAULT NULL COMMENT '创建时间',
  `sys_update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '系统更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='粉丝表(其他人关注了我)';


CREATE TABLE  IF NOT EXISTS `feedback` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `user_id` varchar(128) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '用户id(如果用户没有登录,就是sessionId)',
  `feedback_id` varchar(32) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '反馈id',
  `feedback_content` text COLLATE utf8mb4_bin COMMENT '反馈内容',
  `status` int(2) DEFAULT '0' COMMENT '状态 0-正常 1-处理中 2-已处理 3-验收中(限定3天内验收) 4-已关闭  (进度要显示在页面中)',
  `reply` varchar(512) COLLATE utf8mb4_bin DEFAULT NULL COMMENT 'cv站长回复(主要是处理情况,以及感谢)',
  `create_time` bigint(11) DEFAULT NULL COMMENT '创建时间',
  `sys_update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '系统更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC COMMENT='反馈表\n\n要让用户感觉到这个网站是活的';


CREATE TABLE  IF NOT EXISTS `member` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `user_id` varchar(32) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '用户id',
  `user_name` varchar(128) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '用户名',
  `status` int(2) DEFAULT '0' COMMENT '用户状态 0-正常 1-禁用 2-删除',
  `email` varchar(128) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '邮件',
  `phone` varchar(24) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '电话',
  `password` varchar(43) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '密码',
  `via` varchar(512) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '头像',
  `create_time` bigint(11) DEFAULT NULL COMMENT '创建时间',
  `sys_update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '系统更新时间',
  `last_login_time` bigint(11) DEFAULT '0' COMMENT '最近登录时间',
  `statement_id` int(2) DEFAULT NULL COMMENT '声明id',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `index_member_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC COMMENT='会员表';


CREATE TABLE  IF NOT EXISTS `member_extend` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `user_id` varchar(32) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '用户id(关联member)',
  `birthday` varchar(32) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '生日',
  `age` int(2) DEFAULT NULL COMMENT '年龄',
  `emergency_code` varchar(43) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '紧急代码(用户忘记密码使用-界面有入口可以设置)只能使用一次_成功使用一次后,必须强制更换,如果不填,默认生成一串密文',
  `introduce` text COLLATE utf8mb4_bin COMMENT '自我介绍',
  `sys_update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '系统更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `index_member_extend_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC COMMENT='用户拓展表';


CREATE TABLE  IF NOT EXISTS `member_integral` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `integral_id` varchar(32) NOT NULL COMMENT '积分id',
  `integral_user_id` varchar(32) NOT NULL COMMENT '积分所属用户id',
  `integral_rule_id` bigint(11) DEFAULT NULL COMMENT '积分规则id 积分规则 1-每日登录 2-新用户注册 3-新增文章 4-新增话题 5-发表评论 6-关注作者 7-收藏内容 8-网站反馈并被采纳 9-完善个人简介 10-统计新增评论数 11-每日签到 12-留言送积分 13-点赞送积分',
  `target_id` varchar(32) DEFAULT NULL COMMENT '目标id',
  `integral_value` bigint(11) DEFAULT NULL COMMENT '积分值',
  `integral_text` varchar(1024) DEFAULT NULL COMMENT '积分描述',
  `status` int(2) DEFAULT NULL COMMENT '状态 0-正常 1-删除',
  `remark` varchar(256) DEFAULT NULL COMMENT '备注',
  `create_time` bigint(11) DEFAULT NULL COMMENT '创建时间',
  `sys_update_time` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '系统变更时间',
  PRIMARY KEY (`id`),
  KEY `index_member_integral_integral_user_id` (`integral_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户积分';


CREATE TABLE  IF NOT EXISTS `message` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `message_id` varchar(32) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '消息id',
  `user_id` varchar(32) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '用户id',
  `message_type` int(2) DEFAULT NULL COMMENT '消息类型 0-系统消息 1-用户回复',
  `message` varchar(1024) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '消息(里面可以接入推广信息)',
  `status` int(2) DEFAULT NULL COMMENT '状态 0-未读 1-已读 2-删除',
  `create_time` bigint(11) DEFAULT NULL COMMENT '创建时间',
  `sys_update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '系统更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC COMMENT='消息';

CREATE TABLE  IF NOT EXISTS `seo` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `seo_id` varchar(32) DEFAULT NULL,
  `seo_content` varchar(256) DEFAULT NULL COMMENT 'seo内容(主要存储页面url)',
  `seo_status` int(11) DEFAULT NULL COMMENT '状态0-正常',
  `create_time` bigint(20) DEFAULT NULL COMMENT '创建时间',
  `sys_update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '系统更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='站点地图SEO';


CREATE TABLE  IF NOT EXISTS `slogan` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `slogan_id` varchar(32) DEFAULT NULL COMMENT 'sloganid',
  `slogan_name` varchar(128) DEFAULT NULL COMMENT 'slogan名字 (固定:tech,qa,share,workplace)',
  `slogan_text` varchar(512) DEFAULT NULL COMMENT '标语文字',
  `slogan_type` int(2) DEFAULT NULL COMMENT 'slogan类型 0-页面',
  `slogan_status` int(2) DEFAULT NULL COMMENT '状态 0-正常 1-删除',
  `create_time` bigint(11) DEFAULT NULL COMMENT '创建时间',
  `sys_update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '系统更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='标语';


CREATE TABLE  IF NOT EXISTS `statement` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `statement_id` int(2) DEFAULT NULL COMMENT '声明id',
  `title` varchar(128) DEFAULT NULL COMMENT '标题',
  `statement` varchar(256) DEFAULT NULL COMMENT '声明',
  `create_time` bigint(11) DEFAULT NULL COMMENT '创建时间',
  `sys_update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '系统更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE  IF NOT EXISTS `tag` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `tag_id` varchar(32) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '标签id',
  `tag_type` int(2) DEFAULT NULL COMMENT '标签类型 1-技术 2-问答  3-分享 4-职场 5-记录',
  `tag_name` varchar(32) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '标签名',
  `status` int(11) DEFAULT NULL COMMENT '状态 0-正常 1-删除',
  `icon` varchar(256) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '图标',
  `create_time` bigint(11) DEFAULT NULL COMMENT '创建时间',
  `sys_update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '系统更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `index_tag_tag_id` (`tag_id`),
  KEY `index_tag_tag_name` (`tag_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='标签  注意事项: 标签名不可与菜单名一直,否则会出现激活相应的菜单栏问题\n标签分类:\n\n|— 技术页面\n     |— linux Linux相关\n     |— java Java相关\n|— 分享\n|— 问答\n|— 职场';
