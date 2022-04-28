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

package com.upupor.service.data.service;


import com.upupor.service.data.dao.entity.Viewer;
import com.upupor.service.dto.page.common.ListViewHistoryDto;
import com.upupor.service.types.ViewTargetType;

import java.util.List;

/**
 * 访问者服务
 *
 * @author YangRunkang(cruise)
 * @date 2021/01/27 23:21
 */
public interface ViewerService {

    /**
     * 添加访问者
     *
     * @param targetId
     * @param targetType
     * @return
     */
    void addViewer(String targetId, ViewTargetType targetType);


    /**
     * 根据根据目标Id获取访问者
     *
     * @param targetId
     * @return
     */
    List<Viewer> listViewerByTargetIdAndType(String targetId, ViewTargetType targetType);


    /**
     * 根据用户id获取其浏览记录
     * @param userId
     * @param pageNum
     * @param pageSize
     * @return
     */
    ListViewHistoryDto listViewHistoryByUserId(String userId, Integer pageNum, Integer pageSize);

}
