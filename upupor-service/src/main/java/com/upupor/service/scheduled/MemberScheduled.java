package com.upupor.service.scheduled;

import com.alibaba.fastjson.JSON;
import com.upupor.service.dao.entity.Member;
import com.upupor.service.dto.cache.CacheMemberDto;
import com.upupor.service.service.MemberService;
import com.upupor.service.utils.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import static com.upupor.service.common.CcConstant.CvCache.ACTIVE_USER_LIST;


/**
 * @description: 用户相关的定时任务
 * @author: cruise
 * @create: 2020-04-24 12:54
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class MemberScheduled {

    private final MemberService memberService;

    /**
     * 统计活跃用户
     * 每 6 分钟更新一次缓存
     */
    @Scheduled(cron = "0 0/6 * * * ? ")
    public void globalContent() {
        log.info("统计活跃用户任务启动");
        List<Member> memberList = memberService.activeMember();
        if (CollectionUtils.isEmpty(memberList)) {
            memberList = new ArrayList<>();
        }
        CacheMemberDto cacheMemberDto = new CacheMemberDto();
        cacheMemberDto.setMemberList(memberList);
        RedisUtil.set(ACTIVE_USER_LIST, JSON.toJSONString(cacheMemberDto));
    }

}
