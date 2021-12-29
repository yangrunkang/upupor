package com.upupor.service.service.aggregation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.upupor.framework.utils.CcDateUtil;
import com.upupor.framework.utils.SpringContextUtils;
import com.upupor.service.common.*;
import com.upupor.service.dao.entity.*;
import com.upupor.service.dao.mapper.*;
import com.upupor.service.dto.page.common.ListDailyPointsMemberDto;
import com.upupor.service.dto.page.common.ListMemberDto;
import com.upupor.service.listener.event.MemberLoginEvent;
import com.upupor.service.service.aggregation.service.*;
import com.upupor.service.utils.*;
import com.upupor.spi.req.*;
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

import static com.upupor.service.common.ErrorCode.DATA_EXCEPTION;

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
    private final ContentDataMapper contentDataMapper;
    private final MemberIntegralService memberIntegralService;
    @Resource
    private FanService fanService;
    private final StatementMapper statementMapper;
    private final CssPatternService cssPatternService;
    private final MemberConfigMapper memberConfigMapper;
    private final FileService fileService;
    private final ApplicationEventPublisher eventPublisher;

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
        member.setStatus(CcEnum.MemberStatus.NORMAL.getStatus());
        member.setVia(AvatarHelper.generateAvatar(Math.abs(member.getUserId().hashCode())));
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
            memberList = memberMapper.selectByEmail(addMemberReq.getEmail());
        }
        toCheck(memberList);

        if (!StringUtils.isEmpty(addMemberReq.getPhone())) {
            memberList = memberMapper.selectByPhone(addMemberReq.getPhone());
        }
        toCheck(memberList);
    }

    private void toCheck(List<Member> memberList) {
        if (!CollectionUtils.isEmpty(memberList) && memberList.size() >= 1) {
            throw new BusinessException(ErrorCode.MEMBER_ALREADY_EXISTS);
        }
    }

    @Override
    public Boolean login(GetMemberReq getMemberReq) {
        // 优先使用 紧急代码
        if (!StringUtils.isEmpty(getMemberReq.getEmergencyCode())) {
            String md5EmergencyCode = PasswordUtils.encryptMemberEmergencyCode(getMemberReq.getEmergencyCode());
            boolean b = memberExtendMapper.countByEmergencyCode(md5EmergencyCode) > 0;
            if (b) {
                // 登录成功 重新设置EmergencyCode
            }
            return b;
        }

        // 其次是常规登录
        if (StringUtils.isEmpty(getMemberReq.getPassword())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "密码为空");
        }

        List<Member> members = memberMapper.selectByEmail(getMemberReq.getEmail());
        if (CollectionUtils.isEmpty(members)) {
            throw new BusinessException(ErrorCode.WITHOUT_USER_PLEASE_TO_REGISTER);
        }

        // 密码加密
        String encryptPassword = PasswordUtils.encryptMemberPassword(getMemberReq.getPassword(), members.get(0));
        // reset password use encrypts
        getMemberReq.setPassword(encryptPassword);
        Member member = memberMapper.select(getMemberReq);
        if (Objects.isNull(member)) {
            throw new BusinessException(ErrorCode.PASSWORD_ERROR);
        }

        MemberExtend memberExtend = getMemberExtend(member.getUserId());
        if (Objects.isNull(memberExtend)) {
            throw new BusinessException(ErrorCode.USER_INFO_LESS);
        }

        // 设置登录成功Session
        ServletUtils.getSession().setAttribute(CcConstant.Session.USER_ID, member.getUserId());
        ServletUtils.getSession().setAttribute(CcConstant.Session.USER_VIA, member.getVia());
        ServletUtils.getSession().setAttribute(CcConstant.Session.USER_NAME, member.getUserName());
        ServletUtils.getSession().setAttribute(CcConstant.Session.IS_ADMIN, member.getIsAdmin());
        if (!StringUtils.isEmpty(memberExtend.getBgImg())) {
            ServletUtils.getSession().setAttribute(CcConstant.Session.USER_BG_IMG, memberExtend.getBgImg());
        }
        // 发送登录成功事件
        MemberLoginEvent memberLoginEvent = new MemberLoginEvent();
        memberLoginEvent.setUserId(member.getUserId());
        eventPublisher.publishEvent(memberLoginEvent);

        return Boolean.TRUE;
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
        if (!member.getStatus().equals(CcEnum.MemberStatus.NORMAL.getStatus())) {
            throw new BusinessException(ErrorCode.USER_STATUS_EXCEPTION);
        }
        member.setMemberExtend(memberExtend);
        member.setMemberConfig(getMemberConfig(userId));
        return member;
    }

    private MemberExtend getMemberExtend(String userId) {
        LambdaQueryWrapper<MemberExtend> queryMemberExtend = new LambdaQueryWrapper<MemberExtend>()
                .eq(MemberExtend::getUserId, userId);
        MemberExtend memberExtend = memberExtendMapper.selectOne(queryMemberExtend);
        Asserts.notNull(memberExtend, ErrorCode.MEMBER_NOT_EXISTS);
        return memberExtend;
    }

    private Member getMember(String userId) {
        LambdaQueryWrapper<Member> queryMember = new LambdaQueryWrapper<Member>()
                .eq(Member::getUserId, userId)
                .eq(Member::getStatus, CcEnum.MemberStatus.NORMAL.getStatus());
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
    public Boolean editMember(UpdateMemberReq updateMemberReq) throws Exception {
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
        LambdaQueryWrapper<MemberConfig> querConfig = new LambdaQueryWrapper<MemberConfig>()
                .eq(MemberConfig::getUserId, userId);
        MemberConfig memberConfig = memberConfigMapper.selectOne(querConfig);
        return memberConfig;
    }

    @Override
    public Boolean editMemberBgStyle(UpdateCssReq updateCssReq) {
        String userId = updateCssReq.getUserId();
        Member member = memberInfo(userId);
        MemberExtend memberExtend = member.getMemberExtend();

        // 如果不为空则是自定义的样式
        if (!StringUtils.isEmpty(updateCssReq.getSelfDefineCss())) {
            CssPattern cssPattern = cssPatternService.getByUserId(updateCssReq.getUserId());
            if (Objects.isNull(cssPattern)) {
                CssPattern newCss = new CssPattern();
                newCss.setUserId(updateCssReq.getUserId());
                newCss.setCssPatternId(CcUtils.getUuId());
                newCss.setCssPatternName("用户自定义");
                newCss.setPatternContent(updateCssReq.getSelfDefineCss());
                Boolean add = cssPatternService.add(newCss);
                if (add) {
                    memberExtend.setBgImg(updateCssReq.getSelfDefineCss());
                }
            } else {
                cssPattern.setPatternContent(updateCssReq.getSelfDefineCss());
                Boolean update = cssPatternService.updateByUserId(cssPattern);
                if (update) {
                    memberExtend.setBgImg(updateCssReq.getSelfDefineCss());
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
    public Boolean completeMemberInfo(CompleteMemberInfoReq completeMemberInfoReq) {
        Member member = memberInfo(completeMemberInfoReq.getUserId());

        member.setVia(completeMemberInfoReq.getVia());
        member.setPhone(completeMemberInfoReq.getPhone());

        MemberExtend memberExtend = member.getMemberExtend();
        memberExtend.setAge(completeMemberInfoReq.getAge());
        memberExtend.setBirthday(completeMemberInfoReq.getBirthday());
        memberExtend.setEmergencyCode(completeMemberInfoReq.getEmergencyCode());
        memberExtend.setIntroduce(completeMemberInfoReq.getIntroduce());
        int count = memberMapper.updateById(member);
        int total = memberExtendMapper.updateById(memberExtend) + count;
        return total > 0;
    }

    @Override
    public Integer total() {
        return memberMapper.total();
    }

    @Override
    public Integer totalNormal() {
        return memberMapper.totalNormal();
    }

    @Override
    public Integer viewTotal() {
        return contentDataMapper.viewTotal();
    }

    @Override
    public List<Member> listByUserIdList(List<String> userIdList) {
        return memberMapper.listByUserIdList(userIdList);
    }

    @Override
    public Boolean resetPassword(UpdatePasswordReq updatePasswordReq) {
        String email = updatePasswordReq.getEmail();
        List<Member> memberList = checkUserExists(email);

        Member member = memberList.get(0);
        member.setEmail(email);
        member.setPassword(PasswordUtils.encryptMemberPassword(updatePasswordReq.getPassword(), member));

        return memberMapper.updateById(member) > 0;
    }

    @Override
    public List<Member> checkUserExists(String email) {
        if (StringUtils.isEmpty(email)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }

        LambdaQueryWrapper<Member> query = new LambdaQueryWrapper<Member>()
                .eq(Member::getEmail, email);
        List<Member> memberList = memberMapper.selectList(query);

        if (CollectionUtils.isEmpty(memberList)) {
            throw new BusinessException(ErrorCode.YOU_EMAIL_HAS_NOT_REGISTERED);
        }
        // 邮箱必须是唯一的
        if (memberList.size() > 1) {
            throw new BusinessException(DATA_EXCEPTION);
        }

        return memberList;
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
                String dailyPointsKey = "DAILY_POINTS_" + format + "_" + userId;
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
    public void renderCardHtml(Member member) {
        Map<String, Object> params = new HashMap<>();
        params.put(CcTemplateConstant.USER_ID, member.getUserId());
        params.put(CcTemplateConstant.USER_NAME, member.getUserName());

        Integer fanNum = this.fansNum(member.getUserId());
        Integer totalIntegral = this.sumIntegral(member.getUserId());
        params.put(CcTemplateConstant.USER_FAN_NUM, fanNum);
        params.put(CcTemplateConstant.USER_INTEGRAL_NUM, totalIntegral);

        member.setCardHtml(HtmlTemplateUtils.renderMemberCardHtml(CcTemplateConstant.MEMBER_CARD_HTML, params));
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

        if (CcEnum.OpenEmail.UN_SUBSCRIBE_EMAIL.getStatus().equals(memberConfig.getOpenEmail())) {
            return true;
        }

        memberConfig.setOpenEmail(CcEnum.OpenEmail.UN_SUBSCRIBE_EMAIL.getStatus());
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

    @Override
    public Boolean isOpenEmail(String userId) {
        Member member = getMember(userId);
        if (Objects.isNull(member)) {
            return false;
        }

        MemberConfig memberConfig = getMemberConfig(userId);
        if (Objects.isNull(memberConfig)) {
            return false;
        }

        return CcEnum.OpenEmail.SUBSCRIBE_EMAIL.getStatus().equals(memberConfig.getOpenEmail());
    }


    /**
     * 历史头像
     *
     * @param userId
     */
    @Override
    public List<String> historyVia(String userId) {
        Member member = getMember(userId);
        if (Objects.isNull(member)) {
            return new ArrayList<>();
        }

        return fileService.getUserHistoryViaList(userId);
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
        memberConfig.setOpenEmail(CcEnum.OpenEmail.SUBSCRIBE_EMAIL.getStatus());
        memberConfig.setSysUpdateTime(new Date());
        return memberConfigMapper.insert(memberConfig);
    }

    @Override
    public void checkIsAdmin() {

        String userId = ServletUtils.getUserId();
        if (!StringUtils.isEmpty(userId)) {
            Member member = memberInfo(userId);
            if (!member.getIsAdmin().equals(CcEnum.MemberIsAdmin.ADMIN.getStatus())) {
                throw new BusinessException(ErrorCode.USER_NOT_ADMIN);
            }
        }
    }
}
