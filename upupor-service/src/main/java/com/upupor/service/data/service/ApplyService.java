/*
 * MIT License
 *
 * Copyright (c) 2021-2022 yangrunkang
 *
 * Author: yangrunkang
 * Email: yangrunkang53@gmail.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.upupor.service.data.aggregation.service;

import com.upupor.service.data.dao.entity.Apply;
import com.upupor.service.outer.req.AddApplyDocumentReq;
import com.upupor.service.outer.req.DelApplyReq;
import com.upupor.service.outer.req.UpdateApplyReq;

import java.io.IOException;

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
     * 根据申请id获取申请
     */
    Apply getByApplyId(String applyId);

    /**
     * 更新申请
     *
     * @param updateApplyReq
     * @return
     */
    Boolean editApply(UpdateApplyReq updateApplyReq);

    /**
     * 提交文件
     *
     * @param addApplyDocumentReq 申请文件
     * @return
     */
    Integer commitDocument(AddApplyDocumentReq addApplyDocumentReq) throws IOException;

    Boolean delApply(DelApplyReq delApplyReq);

}