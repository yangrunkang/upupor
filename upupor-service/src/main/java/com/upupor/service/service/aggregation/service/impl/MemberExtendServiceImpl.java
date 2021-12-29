package com.upupor.service.service.aggregation.service.impl;

import com.upupor.service.dao.entity.MemberExtend;
import com.upupor.service.dao.mapper.MemberExtendMapper;
import com.upupor.service.service.aggregation.service.MemberExtendService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author YangRunkang(cruise)
 * @date 2020/03/29 15:39
 */
@Service
@RequiredArgsConstructor
public class MemberExtendServiceImpl implements MemberExtendService {

    private final MemberExtendMapper memberExtendMapper;

    @Override
    public List<MemberExtend> extendInfoByUserIdList(List<String> userIdList) {

        if (CollectionUtils.isEmpty(userIdList)) {
            return new ArrayList<>();
        }

        return memberExtendMapper.listByUserIdList(userIdList);
    }


}
