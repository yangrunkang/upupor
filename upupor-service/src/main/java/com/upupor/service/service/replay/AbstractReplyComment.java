package com.upupor.service.service.replay;

import com.upupor.service.common.CcEnum;
import com.upupor.service.dao.entity.Member;
import com.upupor.service.listener.event.ReplayCommentEvent;
import com.upupor.service.service.aggregation.service.MemberService;
import com.upupor.service.service.aggregation.service.MessageService;
import com.upupor.service.utils.CcUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Yang Runkang (cruise)
 * @date 2021年12月18日 00:47
 * @email: yangrunkang53@gmail.com
 */
@RequiredArgsConstructor
public abstract class AbstractReplyComment<T> {
    @Getter
    private final MemberService memberService;
    @Getter
    private final MessageService messageService;
    /**
     * 是否需要处理
     * @param targetId
     * @param commentSource
     * @return
     */
    public abstract Boolean isHandled(String targetId, CcEnum.CommentSource commentSource);

    /**
     * 回复
     * @param replayCommentEvent
     */
    public abstract void reply(ReplayCommentEvent replayCommentEvent);

    /**
     * 更新目标的评论者信息
     * @param targetId
     * @param commenterUserId
     */
    public abstract void updateTargetCommentCreatorInfo(String targetId,String commenterUserId);


    /**
     * 获取目标对象
     * @param targetId
     * @return
     */
    protected abstract T getTarget(String targetId);

    protected String msgId(){
        return CcUtils.getUuId();
    }

    protected Member getMember(String userId){
        return memberService.memberInfo(userId);
    }

    protected String title(){
        return "您的评论收到了新的回复";
    }

}
