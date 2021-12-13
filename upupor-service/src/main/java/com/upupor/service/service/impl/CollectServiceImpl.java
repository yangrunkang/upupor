package com.upupor.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.upupor.service.common.BusinessException;
import com.upupor.service.common.ErrorCode;
import com.upupor.service.dao.entity.Collect;
import com.upupor.service.dao.mapper.CollectMapper;
import com.upupor.service.dto.page.common.ListCollectDto;
import com.upupor.service.service.CollectService;
import com.upupor.service.utils.CcUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 收藏服务实现类
 *
 * @author YangRunkang(cruise)
 * @date 2020/01/27 15:13
 */
@Service
@RequiredArgsConstructor
public class CollectServiceImpl implements CollectService {

    private final CollectMapper collectMapper;

    @Override
    public ListCollectDto listByUserId(String userId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Collect> hottestContentIdList = collectMapper.listByUserId(userId);
        PageInfo<Collect> pageInfo = new PageInfo<>(hottestContentIdList);

        ListCollectDto listCollectDto = new ListCollectDto(pageInfo);
        listCollectDto.setCollectList(pageInfo.getList());
        return listCollectDto;
    }

    @Override
    public ListCollectDto listByUserIdManage(String userId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Collect> hottestContentIdList = collectMapper.listByUserIdManage(userId);
        PageInfo<Collect> pageInfo = new PageInfo<>(hottestContentIdList);

        ListCollectDto listCollectDto = new ListCollectDto(pageInfo);
        listCollectDto.setCollectList(pageInfo.getList());
        return listCollectDto;
    }


    @Override
    public Boolean existsCollectContent(String contentId, String userId) {
        if (CcUtils.isAllEmpty(contentId, userId)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        return collectMapper.existsCollectContent(contentId, userId) > 0;
    }

    @Override
    public Boolean existsCollectContent(String contentId, Integer collectStatus, String userId) {
        return collectMapper.getByContentIdAndCollectStatus(contentId, collectStatus, userId) > 0;
    }

    @Override
    public Collect getCollect(String contentId, Integer collectStatus, String userId) {
        return collectMapper.getCollect(contentId, collectStatus, userId);
    }

    @Override
    public Integer addCollect(Collect collect) {
        return collectMapper.insert(collect);
    }


    @Override
    public Integer collectNum(Integer collectType, String collectValue) {
        return collectMapper.collectNum(collectType, collectValue);
    }

    @Override
    public Collect select(LambdaQueryWrapper<Collect> queryCollect) {
        return collectMapper.selectOne(queryCollect);
    }

    @Override
    public Integer update(Collect collect) {
        return collectMapper.updateById(collect);
    }
}
