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

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.upupor.service.data.dao.entity.Collect;
import com.upupor.service.dto.page.common.ListCollectDto;
import com.upupor.service.types.CollectType;

/**
 * 收藏服务
 *
 * @author YangRunkang(cruise)
 * @date 2020/01/27 15:09
 */
public interface CollectService {


    ListCollectDto listByUserIdManage(String userId, Integer pageNum, Integer pageSize);

    /**
     * 检查用户是否已经收藏
     *
     * @param contentId
     * @param userId
     */
    Boolean existsCollectContent(String contentId, String userId);

    /**
     * 添加收藏
     *
     * @param collect
     * @return
     */
    Integer addCollect(Collect collect);


    Integer collectNum(CollectType collectType, String collectValue);

    Collect select(LambdaQueryWrapper<Collect> queryCollect);

    Integer update(Collect collect);
}
