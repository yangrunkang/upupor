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

package com.upupor.framework.common;

import lombok.Getter;

/**
 * 积分枚举
 *
 * @author YangRunkang(cruise)
 * @date 2020/03/07 22:05
 */
@Getter
public enum IntegralEnum {

    /**
     * 每日登录
     */
    THE_DAILY_LOGIN(1, +10, "每日登录"),

    /**
     * 新用户注册,送初始积分
     */
    NEW_REGISTER_USER(2, +2020, "新用户注册,送初始积分"),

    /**
     * 新增文章
     */
    CREATE_CONTENT(3, +20, "新增文章"),

    /**
     * 新增短内容
     */
    CREATE_TOPIC(4, +10, "新增短内容"),

    /**
     * 评论
     */
    CREATE_COMMENT(5, +8, "评论消耗"),

    /**
     * 关注内容作者
     */
    ATTENTION_AUTHOR(6, +5, "关注作者"),

    /**
     * 收藏内容
     */
    COLLECT(7, +5, "收藏"),

    /**
     * 反馈内容并被网站采纳
     */
    FEEDBACK(8, +5, "反馈内容并被网站采纳"),

    /**
     * 完善个人简介
     */
    EDIT_USER_INTRO(9, +8, "完善个人简介"),

    /**
     * 统计新增评论数
     */
    STATISTIC_COMMENT_COUNT(10, +8, "统计新增评论数"),

    /**
     * 每日领取积分
     */
    DAILY_POINTS(11, +30, "每日签到送积分"),

    /**
     * 留言
     */
    MESSAGE(12, +6, "留言送积分"),

    /**
     * 点赞送积分
     */
    CLICK_LIKE(13, +2, "点赞送积分"),

    ;

    /**
     * 规则id
     */
    private final Integer ruleId;

    /**
     * 积分值
     */
    private final Integer integral;

    /**
     * 规则描述
     */
    private final String ruleDesc;

    IntegralEnum(Integer ruleId, Integer integral, String ruleDesc) {
        this.ruleId = ruleId;
        this.integral = integral;
        this.ruleDesc = ruleDesc;
    }

}

