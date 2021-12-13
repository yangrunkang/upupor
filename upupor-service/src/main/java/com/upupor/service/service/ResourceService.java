package com.upupor.service.service;

import com.upupor.service.dao.entity.Resource;
import com.upupor.spi.req.GetResourceReq;
import com.upupor.spi.req.ListResourceReq;

import java.util.List;

/**
 * 资源服务
 *
 * @author: YangRunkang(cruise)
 * @created: 2019/12/23 00:33
 */
public interface ResourceService {

    /**
     * 添加资源
     *
     * @param resource
     * @return
     */
    Integer addResource(Resource resource);

    /**
     * 更新资源
     *
     * @param resource
     * @return
     */
    Integer updateResource(Resource resource);

    /**
     * 获取资源列表
     *
     * @param listResourceReq
     * @return
     */
    List<Resource> listResource(ListResourceReq listResourceReq);

    /**
     * 获取资源
     *
     * @param GetResourceReq
     * @return
     */
    Resource getResource(GetResourceReq GetResourceReq);
}
