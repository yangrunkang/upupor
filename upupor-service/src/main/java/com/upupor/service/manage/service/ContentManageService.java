package com.upupor.service.manage.service;

import com.upupor.service.dto.page.common.ListContentDto;
import com.upupor.spi.req.ListContentReq;

/**
 * 内容管理服务
 *
 * @author YangRunkang(cruise)
 * @date 2020/01/27 17:08
 */
public interface ContentManageService {

    /**
     * 内容列表
     *
     * @param listContentReq
     * @return
     */
    ListContentDto listContent(ListContentReq listContentReq);

    /**
     * @param pageNum
     * @param pageSize
     * @param userId
     * @return
     */
    ListContentDto listContentDraft(Integer pageNum, Integer pageSize, String userId, String searchTitle);

}
