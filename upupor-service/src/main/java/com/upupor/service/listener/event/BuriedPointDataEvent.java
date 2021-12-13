package com.upupor.service.listener.event;

import lombok.Builder;
import lombok.Data;

import javax.servlet.http.HttpServletRequest;

/**
 * 埋点数据时间
 *
 * @author YangRunkang(cruise)
 * @date 2020/01/18 15:09
 */
@Data
@Builder
public class BuriedPointDataEvent {

    private HttpServletRequest request;

    /**
     * 埋点类型
     */
    private Integer pointType;

}
