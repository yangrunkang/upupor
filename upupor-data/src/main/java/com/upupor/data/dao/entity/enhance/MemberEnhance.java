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

package com.upupor.data.dao.entity.enhance;

import com.upupor.data.dao.entity.Member;
import com.upupor.framework.CcRedis;
import com.upupor.framework.utils.CcDateUtil;
import com.upupor.framework.utils.RedisUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.apache.logging.log4j.util.Strings;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Yang Runkang (cruise)
 * @createTime 2022-11-27 03:46
 * @email: yangrunkang53@gmail.com
 */
@Data
@Builder
@AllArgsConstructor
public class MemberEnhance {
    private Member member;
    /**
     * 用户拓展信息
     */
    private MemberExtendEnhance memberExtendEnhance;

    /**
     * 用户配置信息
     */
    private MemberConfigEnhance memberConfigEnhance;

    /**
     * 用户状态信息
     */
    private StatementEnhance statementEnhance;

    private String createDate;
    private String createDateDiff;
    private String lastLoginDate;
    /**
     * 创建时间短 年-月-日
     */
    private String createDateShort;

    /**
     * 页面冗余字段
     */
    private String sysUpdateDate;

    /**
     * 粉丝数
     */
    private Integer fansNum;

    /**
     * 关注数
     */
    private Integer attentionNum;

    /**
     * 积分总数
     */
    private Integer totalIntegral;

    /**
     * 文章总数
     */
    private Integer totalContentNum;


    /**
     * 是否活跃
     */
    private Boolean active = false;

    /**
     *
     */
    private List<String> historyViaList;


    public MemberEnhance() {
        this.fansNum = 0;
        this.attentionNum = 0;
        this.totalIntegral = 0;
        this.historyViaList = new ArrayList<>();
        this.createDateDiff = Strings.EMPTY;
    }

    public String getCreateDate() {
        Long createTime = member.getCreateTime();
        if (Objects.nonNull(createTime) && createTime > 0) {
            return CcDateUtil.timeStamp2Date(createTime);
        }
        return createDate;
    }

    public String getLastLoginDate() {
        Long lastLoginTime = member.getLastLoginTime();
        if (Objects.nonNull(lastLoginTime) && lastLoginTime > 0) {
            return CcDateUtil.timeStamp2Date(lastLoginTime);
        }
        return lastLoginDate;
    }

    public String getCreateDateShort() {
        Long createTime = member.getCreateTime();
        if (Objects.nonNull(createTime) && createTime > 0) {
            return CcDateUtil.timeStamp2DateShort(createTime);
        }
        return createDate;
    }

    public Boolean getActive() {
        return RedisUtil.exists(CcRedis.Key.memberActiveKey(getMember().getUserId()));
    }
}
