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

package com.upupor.service.data.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.upupor.framework.BusinessException;
import com.upupor.framework.CcConstant;
import com.upupor.framework.CcRedisKey;
import com.upupor.framework.ErrorCode;
import com.upupor.framework.utils.CcDateUtil;
import com.upupor.framework.utils.CcUtils;
import com.upupor.framework.utils.RedisUtil;
import com.upupor.framework.utils.SpringContextUtils;
import com.upupor.service.common.IntegralEnum;
import com.upupor.service.common.UserCheckFieldType;
import com.upupor.service.data.dao.entity.*;
import com.upupor.service.data.dao.mapper.*;
import com.upupor.service.data.service.*;
import com.upupor.service.dto.page.common.ListDailyPointsMemberDto;
import com.upupor.service.dto.page.common.ListMemberDto;
import com.upupor.service.listener.event.MemberLoginEvent;
import com.upupor.service.outer.req.GetMemberIntegralReq;
import com.upupor.service.outer.req.member.*;
import com.upupor.service.types.*;
import com.upupor.service.utils.Asserts;
import com.upupor.service.utils.AvatarHelper;
import com.upupor.service.utils.PasswordUtils;
import com.upupor.service.utils.ServletUtils;
import com.upupor.service.utils.oss.enums.FileDic;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.upupor.framework.ErrorCode.DATA_EXCEPTION;

/**
 * 实现用户服务
 *
 * @author: YangRunkang(cruise)
 * @created: 2019/12/20 02:57
 */
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private static final String FORMAT_DAY = "yyyy-MM-dd";
    private final MemberMapper memberMapper;
    private final MemberExtendMapper memberExtendMapper;
    private final MemberIntegralMapper memberIntegralMapper;
    private final MemberIntegralService memberIntegralService;
    @Resource
    private FanService fanService;
    private final StatementMapper statementMapper;
    private final MemberConfigMapper memberConfigMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final BusinessConfigService businessConfigService;

    @Override
    public Member register(AddMemberReq addMemberReq) {
        // 邮箱和手机号重要参数检测
        if (CcUtils.isAllEmpty(addMemberReq.getEmail().trim(), addMemberReq.getPhone())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }

        // 注册
        Member member = new Member();
        BeanUtils.copyProperties(addMemberReq, member);
        member.setUserId(CcUtils.getUuId());
        member.setStatus(MemberStatus.NORMAL);

        //  生成头像
        String profileVia = AvatarHelper.generateAvatar(Math.abs(member.getUserId().hashCode()));
        member.setVia(profileVia);
        member.setCreateTime(CcDateUtil.getCurrentTime());
        member.setSysUpdateTime(new Date());
        member.setLastLoginTime(CcDateUtil.getCurrentTime());
        // 加密用户输入的密码
        member.setPassword(PasswordUtils.encryptMemberPassword(addMemberReq.getPassword(), member));

        // 用户拓展表
        MemberExtend memberExtend = new MemberExtend();
        BeanUtils.copyProperties(addMemberReq, memberExtend);
        memberExtend.setUserId(member.getUserId());
        memberExtend.setSysUpdateTime(new Date());
        // 加密用户的紧急Code
        memberExtend.setEmergencyCode(PasswordUtils.encryptMemberEmergencyCode(addMemberReq.getEmergencyCode()));


        int addMember = memberMapper.insert(member);
        int addMemberExtend = memberExtendMapper.insert(memberExtend);
        int addMemberConfig = initMemberConfig(member.getUserId());
        int total = addMemberExtend + addMember + addMemberConfig;
        if (total == 3) {
            // 注册成功后,自动登录设置session
            setLoginUserSession(member, memberExtend);
            return member;
        }
        return null;
    }

    /**
     * 邮箱和收集好两个维度进行检测
     *
     * @param addMemberReq
     */
    @Override
    public void checkUserExists(AddMemberReq addMemberReq) {
        List<Member> memberList = new ArrayList<>();

        if (!StringUtils.isEmpty(addMemberReq.getEmail())) {
            memberList = selectByEmail(addMemberReq.getEmail());
        }
        toCheck(memberList);

        if (!StringUtils.isEmpty(addMemberReq.getPhone())) {
            LambdaQueryWrapper<Member> query = new LambdaQueryWrapper<Member>().eq(Member::getPhone, addMemberReq.getPhone()).eq(Member::getStatus, MemberStatus.NORMAL);
            memberList = memberMapper.selectList(query);
        }
        toCheck(memberList);
    }

    private void toCheck(List<Member> memberList) {
        if (!CollectionUtils.isEmpty(memberList) && memberList.size() >= 1) {
            throw new BusinessException(ErrorCode.MEMBER_ALREADY_EXISTS);
        }
    }

    @Override
    public Boolean login(MemberLoginReq memberLoginReq) {
        // 优先使用 紧急代码
        if (!StringUtils.isEmpty(memberLoginReq.getEmergencyCode())) {
            String md5EmergencyCode = PasswordUtils.encryptMemberEmergencyCode(memberLoginReq.getEmergencyCode());
            boolean b = memberExtendMapper.countByEmergencyCode(md5EmergencyCode) > 0;
            if (b) {
                // 登录成功 重新设置EmergencyCode
            }
            return b;
        }

        // 其次是常规登录
        if (StringUtils.isEmpty(memberLoginReq.getPassword())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "密码为空");
        }
        List<Member> members = selectByEmail(memberLoginReq.getEmail());
        if (CollectionUtils.isEmpty(members)) {
            throw new BusinessException(ErrorCode.WITHOUT_USER_PLEASE_TO_REGISTER);
        }

        // 密码加密
        String encryptPassword = PasswordUtils.encryptMemberPassword(memberLoginReq.getPassword(), members.get(0));
        // reset password use encrypts
        memberLoginReq.setPassword(encryptPassword);
        Member member = memberMapper.select(memberLoginReq);
        if (Objects.isNull(member)) {
            throw new BusinessException(ErrorCode.PASSWORD_ERROR);
        }

        MemberExtend memberExtend = getMemberExtend(member.getUserId());
        if (Objects.isNull(memberExtend)) {
            throw new BusinessException(ErrorCode.USER_INFO_LESS);
        }

        // 设置登录成功Session
        setLoginUserSession(member, memberExtend);

        // 发送登录成功事件
        MemberLoginEvent memberLoginEvent = new MemberLoginEvent();
        memberLoginEvent.setUserId(member.getUserId());
        eventPublisher.publishEvent(memberLoginEvent);

        return Boolean.TRUE;
    }

    private void setLoginUserSession(Member member, MemberExtend memberExtend) {
        ServletUtils.getSession().setAttribute(CcConstant.Session.USER_ID, member.getUserId());
        ServletUtils.getSession().setAttribute(CcConstant.Session.USER_VIA, member.getVia());
        ServletUtils.getSession().setAttribute(CcConstant.Session.USER_NAME, member.getUserName());
        ServletUtils.getSession().setAttribute(CcConstant.Session.IS_ADMIN, MemberIsAdmin.ADMIN.equals(member.getIsAdmin()));
        ServletUtils.getSession().setAttribute(CcConstant.Session.LONG_TIME_UN_UPDATE_PROFILE_PHOTO, isReplaceSystemProfilePhoto(member));
        if (!StringUtils.isEmpty(memberExtend.getBgImg())) {
            ServletUtils.getSession().setAttribute(CcConstant.Session.USER_BG_IMG, memberExtend.getBgImg());
        }
    }

    private Boolean isReplaceSystemProfilePhoto(Member member) {
        long currentTime = CcDateUtil.getCurrentTime();
        Long createTime = member.getCreateTime();
        // 超过7天,未更换头像,给予提示
        if (currentTime - createTime > 60 * 60 * 24 * 7 && member.getVia().contains(FileDic.PROFILE_SYSTEM.getDic())) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    private List<Member> selectByEmail(String email) {
        LambdaQueryWrapper<Member> query = new LambdaQueryWrapper<Member>().eq(Member::getEmail, email).eq(Member::getStatus, MemberStatus.NORMAL);
        return memberMapper.selectList(query);
    }


    @Override
    public Boolean exists(String userId) {

        Member member = getMember(userId);
        if (Objects.isNull(member)) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    @Override
    public Member memberInfo(String userId) {
        Member member = getMember(userId);
        MemberExtend memberExtend = getMemberExtend(userId);
        // 检查用户状态
        if (!member.getStatus().equals(MemberStatus.NORMAL)) {
            throw new BusinessException(ErrorCode.USER_STATUS_EXCEPTION);
        }
        member.setMemberExtend(memberExtend);
        member.setMemberConfig(getMemberConfig(userId));
        return member;
    }

    private MemberExtend getMemberExtend(String userId) {
        LambdaQueryWrapper<MemberExtend> queryMemberExtend = new LambdaQueryWrapper<MemberExtend>().eq(MemberExtend::getUserId, userId);
        MemberExtend memberExtend = memberExtendMapper.selectOne(queryMemberExtend);
        Asserts.notNull(memberExtend, ErrorCode.MEMBER_NOT_EXISTS);
        return memberExtend;
    }

    private Member getMember(String userId) {
        LambdaQueryWrapper<Member> queryMember = new LambdaQueryWrapper<Member>().eq(Member::getUserId, userId).eq(Member::getStatus, MemberStatus.NORMAL);
        Member member = memberMapper.selectOne(queryMember);
        Asserts.notNull(member, ErrorCode.MEMBER_NOT_EXISTS);
        return member;
    }

    @Override
    public Member memberInfoData(String userId) {
        Member member = memberInfo(userId);

        // 获取粉丝数
        member.setFansNum(fanService.getFansNum(member.getUserId()));

        // 获取关注数
        AttentionService attentionService = SpringContextUtils.getBean(AttentionService.class);
        member.setAttentionNum(attentionService.getAttentionNum(member.getUserId()));

        // 获取积分数
        member.setTotalIntegral(memberIntegralService.getUserIntegral(userId));

        return member;
    }

    @Override
    public Boolean editMember(UpdateMemberReq updateMemberReq) {
        String userId = updateMemberReq.getUserId();
        Member member = memberInfo(userId);
        // 编辑用户信息
        BeanUtils.copyProperties(updateMemberReq, member);
        // 邮箱不能编辑
        member.setEmail(null);
        // 编辑用户拓展信息
        MemberExtend memberExtend = member.getMemberExtend();
        BeanUtils.copyProperties(updateMemberReq, memberExtend);
        member.setMemberExtend(memberExtend);
        // 编辑用户配置信息
        MemberConfig memberConfig = getMemberConfig(userId);
        if (Objects.nonNull(memberConfig)) {
            memberConfig.setOpenEmail(updateMemberReq.getOpenEmail());
            memberConfigMapper.updateById(memberConfig);
        }

        // 更新
        int updateMember = memberMapper.updateById(member);
        int updateMemberExtend = memberExtendMapper.updateById(memberExtend);

        return updateMember + updateMemberExtend > 0;
    }

    private MemberConfig getMemberConfig(String userId) {
        List<MemberConfig> memberConfigList = this.listMemberConfig(Lists.newArrayList(userId));
        if (CollectionUtils.isEmpty(memberConfigList)) {
            return null;
        }
        return memberConfigList.get(0);
    }


    @Override
    public Boolean editMemberBgStyle(UpdateCssReq updateCssReq) {
        String userId = updateCssReq.getUserId();
        Member member = memberInfo(userId);
        MemberExtend memberExtend = member.getMemberExtend();

        // 如果不为空则是自定义的样式
        if (!StringUtils.isEmpty(updateCssReq.getSelfDefineCss())) {
            BusinessConfig cssPattern = businessConfigService.getUserDefinedBgStyle(updateCssReq.getUserId());
            if (Objects.isNull(cssPattern)) {
                BusinessConfig newCss = new BusinessConfig();
                newCss.setUserId(updateCssReq.getUserId());
                newCss.setName("用户自定义");
                newCss.setValue(updateCssReq.getSelfDefineCss());
                newCss.setType(BusinessConfigType.CSS_BG);
                newCss.setStatus(BusinessConfigStatus.NORMAL);
                Boolean add = businessConfigService.add(newCss);
                if (add) {
                    memberExtend.setBgImg(newCss.getValue());
                }
            } else {
                cssPattern.setValue(updateCssReq.getSelfDefineCss());
                Boolean update = businessConfigService.update(cssPattern);
                if (update) {
                    memberExtend.setBgImg(cssPattern.getValue());
                }
            }

        }

        if (!StringUtils.isEmpty(updateCssReq.getCssPatternValue())) {
            memberExtend.setBgImg(updateCssReq.getCssPatternValue());
        }

        int total = memberExtendMapper.updateById(memberExtend);
        boolean result = total > 0;
        if (result) {
            if (!StringUtils.isEmpty(memberExtend.getBgImg())) {
                ServletUtils.getSession().setAttribute(CcConstant.Session.USER_BG_IMG, memberExtend.getBgImg());
            }
        }

        return result;
    }

    @Override
    public Boolean update(Member member) {
        int total = 0;
        total = total + memberMapper.updateById(member);
        if (Objects.nonNull(member.getMemberExtend())) {
            total = total + memberExtendMapper.updateById(member.getMemberExtend());
        }
        return total > 0;
    }


    @Override
    public Integer total() {
        return memberMapper.total();
    }


    @Override
    public List<Member> listByUserIdList(List<String> userIdList) {
        return memberMapper.listByUserIdList(userIdList);
    }

    @Override
    public Boolean resetPassword(UpdatePasswordReq updatePasswordReq) {
        String email = updatePasswordReq.getEmail();
        LambdaQueryWrapper<Member> query = new LambdaQueryWrapper<Member>()
                .eq(Member::getEmail, email);
        Member member = memberMapper.selectOne(query);
        member.setEmail(email);
        member.setPassword(PasswordUtils.encryptMemberPassword(updatePasswordReq.getPassword(), member));

        return memberMapper.updateById(member) > 0;
    }

    @Override
    public Boolean checkUserExists(String field, UserCheckFieldType userCheckFieldType) {
        if (StringUtils.isEmpty(field)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }

        LambdaQueryWrapper<Member> query = new LambdaQueryWrapper<Member>()
                .eq(UserCheckFieldType.EMAIL.equals(userCheckFieldType), Member::getEmail, field)
                .eq(UserCheckFieldType.USER_NAME.equals(userCheckFieldType), Member::getUserName, field);
        List<Member> memberList = memberMapper.selectList(query);
        // 数据必须是唯一的
        if (memberList.size() > 1) {
            throw new BusinessException(DATA_EXCEPTION);
        }
        return !CollectionUtils.isEmpty(memberList);
    }

    @Override
    public ListMemberDto list(Integer pageNum, Integer pageSize) {

        PageHelper.startPage(pageNum, pageSize);
        List<Member> memberList = memberMapper.list();
        PageInfo<Member> pageInfo = new PageInfo<>(memberList);

        ListMemberDto listMemberDto = new ListMemberDto(pageInfo);
        listMemberDto.setMemberList(pageInfo.getList());

        return listMemberDto;
    }


    @Override
    public List<Member> activeMember() {
        PageHelper.startPage(CcConstant.Page.NUM, CcConstant.Page.SIZE_TEN);
        List<Member> memberList = memberMapper.activeMember();
        PageInfo<Member> pageInfo = new PageInfo<>(memberList);
        return pageInfo.getList();
    }

    @Override
    public ListDailyPointsMemberDto dailyPointsMemberList() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String format = sdf.format(new Date());
        String startTimeStr = CcDateUtil.date2TimeStamp(format, "yyyy-MM-dd");
        Long startTime = Long.valueOf(startTimeStr);
        Long endTime = startTime + 24 * 60 * 60;


        PageHelper.startPage(CcConstant.Page.NUM, CcConstant.Page.MAX_SIZE);
        List<String> userIdList = memberIntegralMapper.dailyPointsUserIdList(startTime, endTime);
        PageInfo<String> pageInfo = new PageInfo<>(userIdList);
        if (CollectionUtils.isEmpty(pageInfo.getList())) {
            return new ListDailyPointsMemberDto();
        }

        List<Member> memberList = listByUserIdList(userIdList);
        if (CollectionUtils.isEmpty(memberList)) {
            return new ListDailyPointsMemberDto();
        }

        ListDailyPointsMemberDto listDailyPointsMemberDto = new ListDailyPointsMemberDto(pageInfo);
        listDailyPointsMemberDto.setMemberList(memberList);
        return listDailyPointsMemberDto;
    }

    /**
     * 检查是否每日签到
     *
     * @return
     * @throws ParseException
     */
    @Override
    public Boolean checkIsGetDailyPoints() {
        try {
            long startTime;
            long endTime;
            // 局部变量解决 SimpleDateFormat 线程不安全问题
            SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_DAY);
            String format = sdf.format(new Date());
            Date parse = sdf.parse(format);
            startTime = parse.getTime() / 1000;
            endTime = startTime + 24 * 3600;
            String userId = null;
            try {
                userId = ServletUtils.getUserId();
            } catch (Exception e) {
                // 首页不捕获异常,否则会调整到登录页面
            }

            if (Objects.isNull(userId)) {
                // 未登录状态
                return Boolean.FALSE;
            } else {

                String dailyPointsKey = CcRedisKey.dayilyPoint(format, userId);
                if (!Objects.isNull(RedisUtil.get(dailyPointsKey))) {
                    return Boolean.TRUE;
                }

                // 已经登录
                GetMemberIntegralReq getMemberIntegralReq = new GetMemberIntegralReq();
                getMemberIntegralReq.setUserId(userId);
                getMemberIntegralReq.setRuleId(IntegralEnum.DAILY_POINTS.getRuleId());
                getMemberIntegralReq.setStartCreateTime(startTime);
                getMemberIntegralReq.setEndCreateTime(endTime);
                Boolean exists = memberIntegralService.checkExists(getMemberIntegralReq);
                if (exists) {
                    RedisUtil.set(dailyPointsKey, "DONE", 24 * 3600L);
                    return Boolean.TRUE;
                }
            }

            return Boolean.FALSE;
        } catch (ParseException e) {
            return Boolean.FALSE;
        }
    }


    @Override
    public Integer totalIntegral(String userId) {
        if (StringUtils.isEmpty(userId)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }

        return memberIntegralService.getUserIntegral(userId);
    }

    @Override
    public Integer countUnActivityMemberList() {
        return memberMapper.countUnActivityMemberList(CcDateUtil.getCurrentTime());
    }

    @Override
    public List<Member> listUnActivityMemberList(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Member> memberList = memberMapper.listUnActivityMemberList(CcDateUtil.getCurrentTime());
        PageInfo<Member> pageInfo = new PageInfo<>(memberList);
        return pageInfo.getList();
    }

    @Override
    public void bindStatement(Member member) {
        checkNull(member);
        Integer statementId = member.getStatementId();
        if (Objects.nonNull(statementId)) {
            Statement statement = statementMapper.getByStatementId(statementId);
            if (Objects.nonNull(statement)) {
                member.setStatement(statement);
            }
        }
    }

    @Override
    public void checkNull(Member member) {
        if (Objects.isNull(member)) {
            throw new BusinessException(ErrorCode.MEMBER_NOT_EXISTS);
        }
    }

    @Override
    public Integer fansNum(String userId) {
        return fanService.getFansNum(userId);
    }

    @Override
    public Integer sumIntegral(String userId) {
        return memberIntegralService.getUserIntegral(userId);
    }

    @Override
    public void bindRadioMember(Radio radio) {
        if (Objects.isNull(radio)) {
            throw new BusinessException(ErrorCode.RADIO_NOT_EXISTS);
        }

        if (StringUtils.isEmpty(radio.getUserId())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }

        String userId = radio.getUserId();
        Member member = this.memberInfo(userId);
        if (Objects.isNull(member)) {
            throw new BusinessException(ErrorCode.MEMBER_NOT_EXISTS);
        }

        radio.setMember(member);
    }

    @Override
    public Boolean unSubscribeMail(String userId) {
        Member member = getMember(userId);
        if (Objects.isNull(member)) {
            return false;
        }

        MemberConfig memberConfig = getMemberConfig(userId);
        if (Objects.isNull(memberConfig)) {
            return false;
        }

        if (OpenEmail.UN_SUBSCRIBE_EMAIL.equals(memberConfig.getOpenEmail())) {
            return true;
        }

        memberConfig.setOpenEmail(OpenEmail.UN_SUBSCRIBE_EMAIL);
        return memberConfigMapper.updateById(memberConfig) > 0;
    }

    @Override
    public void updateActiveTime(String userId) {
        Member member = getMember(userId);
        if (Objects.isNull(member)) {
            return;
        }

        member.setLastLoginTime(CcDateUtil.getCurrentTime());
        memberMapper.updateById(member);
    }

    /**
     * 初始化用户配置
     *
     * @param userId
     * @return
     */
    @Override
    public int initMemberConfig(String userId) {
        MemberConfig memberConfig = new MemberConfig();
        memberConfig.setConfigId(CcUtils.getUuId());
        memberConfig.setUserId(userId);
        memberConfig.setCreateTime(CcDateUtil.getCurrentTime());
        memberConfig.setOpenEmail(OpenEmail.SUBSCRIBE_EMAIL);
        memberConfig.setSysUpdateTime(new Date());
        return memberConfigMapper.insert(memberConfig);
    }

    @Override
    public void checkIsAdmin() {

        String userId = ServletUtils.getUserId();
        if (!StringUtils.isEmpty(userId)) {
            Member member = memberInfo(userId);
            if (!member.getIsAdmin().equals(MemberIsAdmin.ADMIN)) {
                throw new BusinessException(ErrorCode.USER_NOT_ADMIN);
            }
        }
    }

    @Override
    public List<MemberConfig> listMemberConfig(List<String> userIdList) {
        LambdaQueryWrapper<MemberConfig> listConfigQuery = new LambdaQueryWrapper<MemberConfig>().in(MemberConfig::getUserId, userIdList);
        return memberConfigMapper.selectList(listConfigQuery);
    }

    @Override
    public Boolean updateMemberConfig(MemberConfig memberConfig) {
        Asserts.notNull(memberConfig.getId(), ErrorCode.MEMBER_CONFIG_LESS);
        return memberConfigMapper.updateById(memberConfig) > 0;
    }
}
