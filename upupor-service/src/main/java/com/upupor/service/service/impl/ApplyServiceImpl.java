package com.upupor.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.upupor.service.dao.entity.Apply;
import com.upupor.service.dao.entity.ApplyDocument;
import com.upupor.service.dao.mapper.ApplyDocumentMapper;
import com.upupor.service.dao.mapper.ApplyMapper;
import com.upupor.service.service.ApplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 申请服务
 *
 * @author YangRunkang(cruise)
 * @date 2020/01/24 11:35
 */
@Service
@RequiredArgsConstructor
public class ApplyServiceImpl implements ApplyService {

    private final ApplyMapper applyMapper;

    private final ApplyDocumentMapper applyDocumentMapper;

    @Override
    public Integer addApply(Apply apply) {
        return applyMapper.insert(apply);
    }

    @Override
    public Integer total() {
        return applyMapper.total();
    }

    @Override
    public Apply getByApplyId(String applyId) {
        LambdaQueryWrapper<Apply> query = new LambdaQueryWrapper<Apply>()
                .eq(Apply::getApplyId, applyId);
        return applyMapper.selectOne(query);
    }

    @Override
    public Integer update(Apply apply) {
        return applyMapper.updateById(apply);
    }

    @Override
    public Integer commitDocument(ApplyDocument applyDocument) {
        return applyDocumentMapper.insert(applyDocument);
    }

}
