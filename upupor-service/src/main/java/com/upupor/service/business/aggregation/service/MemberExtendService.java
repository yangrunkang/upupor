package com.upupor.service.business.aggregation.service;

import com.upupor.service.dao.entity.MemberExtend;

import java.util.List;

/**
 * 用户拓展服务
 *
 * @author YangRunkang(cruise)
 * @date 2020/03/29 15:38
 */
public interface MemberExtendService {

    List<MemberExtend> extendInfoByUserIdList(List<String> userIdList);

}