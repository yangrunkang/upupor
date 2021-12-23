package com.upupor.service.listener;

import com.upupor.service.common.CcEnum;
import com.upupor.service.listener.abstracts.AbstractComment;
import com.upupor.service.listener.abstracts.AbstractReplyComment;
import com.upupor.service.listener.event.ReplayCommentEvent;
import com.upupor.service.listener.event.ToCommentSuccessEvent;
import com.upupor.service.service.ContentService;
import com.upupor.service.service.RadioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * upupor 网站站内信
 *
 * @author YangRunkang(cruise)
 * @date 2020/02/06 19:21
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class CommentListener {

    private final ContentService contentService;
    private final RadioService radioService;

    private final List<AbstractComment> abstractCommentList;
    private final List<AbstractReplyComment> abstractReplyCommentList;


    /**
     * 添加评论成功
     *
     * @param event
     */
    @EventListener
    @Async
    public void toCommentSuccessEvent(ToCommentSuccessEvent event) {


        for (AbstractComment abstractComment : abstractCommentList) {
            try {
                if (abstractComment.confirmSource(event.getCommentSource(), event.getTargetId())) {
                    // 评论时间
                    abstractComment.comment(event.getTargetId(), event.getCommenterUserId(), event.getCommentId());
                    // 记录评论人和评论时间
                    abstractComment.updateTargetCommentCreatorInfo(event.getTargetId(), event.getCommenterUserId());
                }
            } catch (Exception e) {
            }
        }
    }


    /**
     * 回复评论事件
     *
     * @param replayCommentEvent
     */
    @EventListener
    @Async
    public void replayCommentEvent(ReplayCommentEvent replayCommentEvent) {
        String beRepliedUserId = replayCommentEvent.getBeRepliedUserId();
        String createReplayUserId = replayCommentEvent.getCreateReplayUserId();

        // 如果自己回复自己则啥也不做
        if (beRepliedUserId.equals(createReplayUserId)) {
            return;
        }

        for (AbstractReplyComment abstractReplyComment : abstractReplyCommentList) {
            try {
                if (abstractReplyComment.isHandled(replayCommentEvent.getTargetId(), CcEnum.CommentSource.getBySource(replayCommentEvent.getCommentSource()))) {
                    abstractReplyComment.reply(replayCommentEvent);
                }
            } catch (Exception e) {
            }
        }
    }


}
