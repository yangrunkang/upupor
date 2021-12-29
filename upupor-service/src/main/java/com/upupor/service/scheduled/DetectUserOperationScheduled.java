package com.upupor.service.scheduled;

import com.upupor.service.common.CcConstant;
import com.upupor.service.dao.entity.Member;
import com.upupor.service.service.aggregation.service.MemberService;
import com.upupor.service.service.aggregation.service.MessageService;
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
    @Scheduled(cron = "0 30 8 ? * MON")
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
