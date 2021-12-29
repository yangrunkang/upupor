package com.upupor.service.business.manage.service;

import com.upupor.service.dto.page.common.ListApplyDto;

/**
 * 申请管理服务
 *
 * @author YangRunkang(cruise)
 * @date 2020/01/27 17:36
 */
public interface ApplyManageService {
    /**
     * 根据用户id获取申请列表
     *
     * @param userId
     * @param pageNum
     * @param pageSize
     * @return
     */
    ListApplyDto listApplyListByUserIdManage(String userId, Integer pageNum, Integer pageSize);

}
