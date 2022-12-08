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

import com.upupor.framework.utils.RedisUtil;


/**
 * @author Yang Runkang (cruise)
 * @createTime 2022-10-30 21:18
 * @email: yangrunkang53@gmail.com
 */
public class CcRedis {
    public static class Key {
        /**
         * 缓存活跃用户
         */
        public static final String ACTIVE_USER_LIST = "activeUserList";

        /**
         * 缓存敏感词
         */
        public static final String CACHE_SENSITIVE_WORD = "cacheSensitiveWord";

        /**
         * 缓存标签对应的文章数目
         */
        public static final String TAG_COUNT = "cvTagList";

        /**
         * 文章发布间隔
         */
        public static final String CREATE_CONTENT_TIME_OUT = "create_content_time_out";
        public static final String REFRESH_KEY = "refresh_key";

        /**
         * SiteMap
         */
        public static final String SITE_MAP = "siteMap";


        /**
         * 创建内容间隔时间Key
         *
         * @param userId
         * @return
         */
        public static String createContentIntervalKey(String userId) {
            return CREATE_CONTENT_TIME_OUT + userId;
        }

        /**
         * 刷新活跃用户列表Key
         *
         * @param userId
         * @return
         */
        public static String refreshActiveMemberListKey(String userId) {
            return REFRESH_KEY + userId;
        }

        /**
         * 每日签到记录Key
         *
         * @param userId
         * @return
         */
        public static String dailyPoint(String format, String userId) {
            return "DAILY_POINTS_" + format + "_" + userId;
        }

        /**
         * 站点地图名
         *
         * @param siteMapName
         * @return
         */
        public static String siteMapKey(String siteMapName) {
            return SITE_MAP + CcConstant.BLANK + siteMapName;
        }

        /**
         * 用户验证码Code
         *
         * @param source
         * @param email
         * @param verifyCode
         * @return
         */
        public static String memberVerifyCodeKey(String source, String email, String verifyCode) {
            return source + email.trim() + verifyCode;
        }


        public static String memberLoginExpiredTimeKey(String userId) {
            return "JWT_EXPIRED_TIME" + "_" + userId;
        }

        /**
         * 用户活跃Key
         *
         * @param userId
         * @return
         */
        public static String memberActiveKey(String userId) {
            return "ACTIVE_MEMBER" + "_" + userId;
        }
    }


    public static class Operate {

        public static long updateTokenExpireTime(String userId) {
            String loginExpiredTimeKey = CcRedis.Key.memberLoginExpiredTimeKey(userId);
            // 24h
            long expiredTime = 24 * 3600;
            RedisUtil.set(loginExpiredTimeKey, String.valueOf(expiredTime), expiredTime);
            return expiredTime;
        }

        /**
         * 用户活跃标识规则,在2分钟内活跃过,都算活跃
         *
         * @param userId
         * @return
         */
        public static void memberActive(String userId) {
            String memberActiveKey = CcRedis.Key.memberActiveKey(userId);
            int minute = 2;
            long activeExpiredTime = minute * 60; //  minute 分钟内内都算活跃
            RedisUtil.set(memberActiveKey, "memberActiveKey:" + userId, activeExpiredTime);
        }


    }
}
