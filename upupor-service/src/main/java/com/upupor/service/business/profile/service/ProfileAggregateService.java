package com.upupor.service.business.profile.service;

import com.upupor.service.business.profile.ProfileAbstract;
import com.upupor.service.common.CcEnum;
import com.upupor.service.dto.page.MemberIndexDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 个人主页聚合服务
 *
 * @author YangRunkang(cruise)
 * @date 2020/01/26 19:41
 */
@Service
@RequiredArgsConstructor
public class ProfileAggregateService {

    private final List<ProfileAbstract> profileAbstractList;

    public MemberIndexDto index(String userId, Integer pageNum, Integer pageSize, CcEnum.ViewTargetType source) {
        for (ProfileAbstract profileAbstract : profileAbstractList) {
            if (profileAbstract.viewTargetType().equals(source)) {
                return profileAbstract.getBusinessData(userId, pageNum, pageSize);
            }
        }
        return new MemberIndexDto();
    }


}
