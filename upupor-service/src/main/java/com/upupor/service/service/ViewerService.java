package com.upupor.service.service;

import com.upupor.service.common.CcEnum;
import com.upupor.service.dao.entity.Viewer;

import java.util.List;

/**
 * 访问者服务
 *
 * @author YangRunkang(cruise)
 * @date 2021/01/27 23:21
 */
public interface ViewerService {

    /**
     * 添加访问者
     *
     * @param targetId
     * @param targetType
     * @return
     */
    void addViewer(String targetId, CcEnum.ViewTargetType targetType);


    /**
     * 根据根据目标Id获取访问者
     *
     * @param targetId
     * @return
     */
    List<Viewer> listViewerByTargetIdAndType(String targetId, CcEnum.ViewTargetType targetType);


}
