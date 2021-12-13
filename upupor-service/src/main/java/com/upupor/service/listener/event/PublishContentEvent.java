package com.upupor.service.listener.event;

import lombok.Data;

/**
 * 用户发表文章
 *
 * @author YangRunkang(cruise)
 * @date 2020/03/07 23:03
 */
@Data
public class PublishContentEvent {

    private String userId;

    private String contentId;

}
