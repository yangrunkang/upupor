package com.upupor.service.manage.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.upupor.service.dao.entity.Apply;
import com.upupor.service.dao.mapper.ApplyMapper;
import com.upupor.service.dto.page.common.ListApplyDto;
import com.upupor.service.manage.service.ApplyManageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 申请管理服务
 *
 * @author YangRunkang(cruise)
 * @date 2020/01/27 17:37
 */
@Service
@RequiredArgsConstructor
public class ApplyManageServiceImpl implements ApplyManageService {
    private final ApplyMapper applyMapper;

    @Override
    public ListApplyDto listApplyListByUserIdManage(String userId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Apply> applyList = applyMapper.listApplyListByUserIdManage(userId);
        PageInfo<Apply> pageInfo = new PageInfo<>(applyList);

        ListApplyDto listApplyDto = new ListApplyDto(pageInfo);
        listApplyDto.setApplyList(applyList);

        return listApplyDto;
    }
}
