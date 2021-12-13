package com.upupor.service.listener.event;

import com.upupor.service.common.CcEnum;
import lombok.Data;

/**
 * 异步记录内容访问者
 *
 * @author YangRunkang(cruise)
 * @date 2021/06/28
 */
@Data
public class ViewerEvent {
    private String targetId;
    private CcEnum.ViewTargetType targetType;
    private String viewerUserId;
}
