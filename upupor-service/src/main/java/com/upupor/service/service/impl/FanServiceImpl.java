package com.upupor.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.upupor.service.common.CcEnum;
import com.upupor.service.dao.entity.Fans;
import com.upupor.service.dao.mapper.FansMapper;
import com.upupor.service.dto.page.common.ListFansDto;
import com.upupor.service.service.FanService;
import com.upupor.service.utils.Asserts;
import com.upupor.spi.req.DelFanReq;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.upupor.service.common.ErrorCode.OBJECT_NOT_EXISTS;

/**
 * 粉丝服务
 *
 * @author YangRunkang(cruise)
 * @date 2020/02/02 14:50
 */
@Service
@RequiredArgsConstructor
public class FanServiceImpl implements FanService {

    private final FansMapper fansMapper;

    @Override
    public int add(Fans fans) {
        return fansMapper.insert(fans);
    }

    @Override
    public int getFansNum(String userId) {
        return fansMapper.getFansNum(userId);
    }

    @Override
    public ListFansDto getFans(String userId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Fans> fans = fansMapper.getFans(userId);
        PageInfo<Fans> pageInfo = new PageInfo<>(fans);

        ListFansDto listFansDto = new ListFansDto(pageInfo);
        listFansDto.setFansList(pageInfo.getList());
        return listFansDto;
    }


    @Override
    public Integer update(Fans fan) {
        return fansMapper.updateById(fan);
    }

    @Override
    public Fans select(LambdaQueryWrapper<Fans> queryFans) {
        return fansMapper.selectOne(queryFans);
    }

    @Override
    public Integer delFans(DelFanReq delFanReq) {
        String fanId = delFanReq.getFanId();

        LambdaQueryWrapper<Fans> query = new LambdaQueryWrapper<Fans>()
                .eq(Fans::getFanId, fanId)
                .eq(Fans::getFanStatus, CcEnum.FansStatus.NORMAL.getStatus());

        Fans fans = select(query);
        Asserts.notNull(fans, OBJECT_NOT_EXISTS);

        fans.setFanStatus(CcEnum.FansStatus.DELETED.getStatus());
        return update(fans);
    }
}
