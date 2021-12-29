package com.upupor.service.business.aggregation.service.impl;

import com.upupor.service.business.aggregation.service.RecommendService;
import com.upupor.service.common.BusinessException;
import com.upupor.service.common.ErrorCode;
import com.upupor.service.dto.page.GlobalIndexDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 全局推广服务
 *
 * @author YangRunkang(cruise)
 * @date 2020/02/01 12:01
 */
@Service
@RequiredArgsConstructor
public class RecommendServiceImpl implements RecommendService {

    @Override
    public GlobalIndexDto globalRecommend() {
//        String s = RedisUtil.get(CcConstant.CvCache.GLOBAL_CONTENT);
//        if (StringUtils.isEmpty(s)) {
//            return new GlobalIndexDto();
//        }
//        GlobalIndexDto globalIndexDto = JSON.parseObject(s, GlobalIndexDto.class);
//        if (Objects.isNull(globalIndexDto)) {
//            return new GlobalIndexDto();
//        }
//        return globalIndexDto;
        throw new BusinessException(ErrorCode.SYSTEM_ERROR);
    }


}
