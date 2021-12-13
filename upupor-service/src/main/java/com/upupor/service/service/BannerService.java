package com.upupor.service.service;

import com.upupor.service.dto.page.common.ListBannerDto;

/**
 * Banner接口
 *
 * @author YangRunkang(cruise)
 * @date 2020/02/04 11:52
 */
public interface BannerService {

    /**
     * 根据状态获取banner
     *
     * @param bannerStatus
     * @param pageNum
     * @param pageSize
     * @return
     */
    ListBannerDto listBannerByStatus(Integer bannerStatus, Integer pageNum, Integer pageSize);


}