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

import com.upupor.service.business.aggregation.dao.entity.Member;
import com.upupor.service.business.aggregation.service.MemberService;
import com.upupor.service.business.aggregation.service.MessageService;
import com.upupor.service.common.CcConstant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

import static com.upupor.service.utils.CcUtils.sleep2s;


/**
 * 检测用户活动等定时任务
 *
 * @author: cruise
 * @created: 2020/06/02 16:57
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DetectUserOperationScheduled {

    private final MemberService memberService;

    private final MessageService messageService;


    /**
     * 检测用户多久未登录
     * 如果超过一周没有登录就邮件
     * <p>
     * 每周一上午8点半
     */
//    @Scheduled(cron = "0 30 8 ? * MON")
    public void detectUserNotLogin() {
        log.info("检测用户一周内未登录定时任务");
        Integer count = memberService.countUnActivityMemberList();
        if (Objects.isNull(count) || count <= 0) {
            return;
        }

        int pageNum = (count % CcConstant.Page.SIZE == 0 ? count / CcConstant.Page.SIZE : count / CcConstant.Page.SIZE + 1);
        for (int i = 1; i <= pageNum; i++) {
            List<Member> memberList = memberService.listUnActivityMemberList(i, CcConstant.Page.SIZE);
            if (CollectionUtils.isEmpty(memberList)) {
                break;
            }
            memberList.forEach(member -> {
                String title = "亲爱的 " + member.getUserName() + " :";
                String content = "<a class='cv-link' target='_blank' href = 'https://www.upupor.com?source=email'>最近一周Upupor更新了很多内容,立即访问</a>";
                messageService.sendEmail(member.getEmail(), title, content, member.getUserId());
                log.info("检测用户一周内未登录定时任务邮件内容:{}-{}-{}", member.getEmail(), title, content);
                sleep2s();
            });
        }

    }


}
