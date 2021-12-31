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

package com.upupor.service.business.aggregation.service;

import com.upupor.service.dao.entity.Member;
import com.upupor.service.dao.entity.Radio;
import com.upupor.service.dto.page.common.ListDailyPointsMemberDto;
import com.upupor.service.dto.page.common.ListMemberDto;
import com.upupor.service.spi.req.*;

import java.util.List;

/**
 * 用户服务
 *
 * @author: YangRunkang(cruise)
 * @created: 2019/12/20 02:56
 */
public interface MemberService {
    /**
     * 注册
     *
     * @param addMemberReq
     * @return
     */
    Member register(AddMemberReq addMemberReq);

    /**
     * 登录Req
     *
     * @param getMemberReq
     * @return
     */
    Boolean login(GetMemberReq getMemberReq);

    /**
     * 检查用户是否存在
     *
     * @param userId
     * @return
     */
    Boolean exists(String userId);

    /**
     * 查看用户信息
     *
     * @param userId
     * @return
     */
    Member memberInfo(String userId);

    /**
     * 包含粉丝数,关注数等数据
     *
     * @param userId
     * @return
     */
    Member memberInfoData(String userId);

    /**
     * 编辑用户信息
     * eg. 更新用户基本信息,忘记密码,清除用户
     *
     * @param updateMemberReq
     * @return
     */
    Boolean editMember(UpdateMemberReq updateMemberReq) throws Exception;

    /**
     * 更新
     *
     * @param member
     * @return
     */
    Boolean update(Member member);

    /**
     * 重设密码
     *
     * @param updatePasswordReq
     * @return
     */
    Boolean resetPassword(UpdatePasswordReq updatePasswordReq);

    /**
     * 完善个人信息
     *
     * @param completeMemberInfoReq
     * @return
     */
    Boolean completeMemberInfo(CompleteMemberInfoReq completeMemberInfoReq);

    /**
     * 注册数(包含已经注销的用户)
     *
     * @return
     */
    Integer total();

    /**
     * 正常的用户总数
     */
    Integer totalNormal();

    /**
     * 文章浏览总数
     *
     * @return
     */
    Integer viewTotal();

    /**
     * 批量根据用户id获取用户名
     *
     * @param userIdList
     * @return
     */
    List<Member> listByUserIdList(List<String> userIdList);

    /**
     * 校验用户是否存在
     *
     * @param addMemberReq
     */
    void checkUserExists(AddMemberReq addMemberReq);

    /**
     * 重置密码校验用户
     *
     * @param email
     */
    List<Member> checkUserExists(String email);


    ListMemberDto list(Integer pageNum, Integer pageSize);

    /**
     * 活跃用户
     *
     * @return
     */
    List<Member> activeMember();

    /**
     * 获取每日签到用户
     *
     * @return
     */
    ListDailyPointsMemberDto dailyPointsMemberList();


    /**
     * 检查今天是否签到
     *
     * @return
     */
    Boolean checkIsGetDailyPoints();

    /**
     * 总积分值
     *
     * @param userId
     * @return
     */
    Integer totalIntegral(String userId);

    /**
     * 统计不活跃用户
     *
     * @return
     */
    Integer countUnActivityMemberList();

    /**
     * @param pageNum
     * @param pageSize
     * @return
     */
    List<Member> listUnActivityMemberList(Integer pageNum, Integer pageSize);

    /**
     * 绑定用户声明
     *
     * @param member
     */
    void bindStatement(Member member);

    /**
     * 检查Null
     *
     * @param member
     */
    void checkNull(Member member);

    /**
     * 编辑网站样式
     *
     * @param updateCssReq
     * @return
     */
    Boolean editMemberBgStyle(UpdateCssReq updateCssReq);

    Integer fansNum(String userId);

    Integer sumIntegral(String userId);


    void renderCardHtml(Member member);

    void bindRadioMember(Radio radio);

    /**
     * 退订邮件
     *
     * @param userId
     * @return
     */
    Boolean unSubscribeMail(String userId);

    /**
     * 跟新活跃时间
     *
     * @param userId
     */
    void updateActiveTime(String userId);

    /**
     * 是否开启邮件订阅
     *
     * @param userId
     */
    Boolean isOpenEmail(String userId);


    /**
     * 历史头像
     *
     * @param userId
     */
    List<String> historyVia(String userId);

    /**
     * 初始化用户配置
     *
     * @param userId
     * @return
     */
    int initMemberConfig(String userId);

    void checkIsAdmin();

}
