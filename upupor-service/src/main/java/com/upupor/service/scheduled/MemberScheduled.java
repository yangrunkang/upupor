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

package com.upupor.service.scheduled;

import com.alibaba.fastjson2.JSON;
import com.upupor.service.data.dao.entity.Member;
import com.upupor.service.data.service.MemberService;
import com.upupor.service.dto.cache.CacheMemberDto;
import com.upupor.framework.utils.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import static com.upupor.framework.CcConstant.CvCache.ACTIVE_USER_LIST;


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
    public void refreshActiveMember() {
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
