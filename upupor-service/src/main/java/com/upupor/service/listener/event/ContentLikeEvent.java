package com.upupor.service.listener.event;

import com.upupor.service.dao.entity.Content;
import lombok.Data;

/**
 * 文章点赞事件
 *
 * @author YangRunkang(cruise)
 * @date 2020/02/06 19:30
 */
@Data
public class ContentLikeEvent {

    private Content content;

    /**
     * 点赞的用户id
     */
    private String clickUserId;

}
