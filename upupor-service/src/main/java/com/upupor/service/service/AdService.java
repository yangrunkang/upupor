package com.upupor.service.service;

import com.upupor.service.dto.page.AdIndexDto;

/**
 * 广告服务
 *
 * @author YangRunkang(cruise)
 * @date 2020/01/25 23:25
 */
public interface AdService {

    /**
     * 根据url获取广告
     *
     * @param requestUrl
     * @return
     */
    AdIndexDto getAdByUrl(String requestUrl);


}
