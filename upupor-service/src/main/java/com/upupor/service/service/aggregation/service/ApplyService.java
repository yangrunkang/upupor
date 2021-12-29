package com.upupor.service.service.aggregation.service;

import com.upupor.service.dao.entity.Apply;
import com.upupor.service.dao.entity.ApplyDocument;

/**
 * 申请服务
 *
 * @author YangRunkang(cruise)
 * @date 2020/01/24 11:34
 */
public interface ApplyService {

    /**
     * 添加申请
     *
     * @param apply
     * @return
     */
    Integer addApply(Apply apply);

    /**
     * 广告总数
     *
     * @return
     */
    Integer total();

    /**
     * 根据申请id获取申请
     */
    Apply getByApplyId(String applyId);

    /**
     * 更新申请
     *
     * @param apply
     * @return
     */
    Integer update(Apply apply);

    /**
     * 提交文件
     *
     * @param applyDocument 申请文件
     * @return
     */
    Integer commitDocument(ApplyDocument applyDocument);

}