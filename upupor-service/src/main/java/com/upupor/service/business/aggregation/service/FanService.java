package com.upupor.service.business.aggregation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.upupor.service.dao.entity.Fans;
import com.upupor.service.dto.page.common.ListFansDto;
import com.upupor.spi.req.DelFanReq;

/**
 * 粉丝服务
 *
 * @author YangRunkang(cruise)
 * @date 2020/02/02 14:50
 */
public interface FanService {

    /**
     * 添加
     *
     * @param fans
     * @return
     */
    int add(Fans fans);

    /**
     * 获取粉丝数
     *
     * @param userId
     * @return
     */
    int getFansNum(String userId);

    /**
     * 获取粉丝
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    ListFansDto getFans(String userId, Integer pageNum, Integer pageSize);


    Integer update(Fans fan);

    Fans select(LambdaQueryWrapper<Fans> queryFans);

    /**
     * 删除粉丝
     *
     * @param delFanReq
     * @return
     */
    Integer delFans(DelFanReq delFanReq);

}
