/*
 * MIT License
 *
 * Copyright (c) 2021 yangrunkang
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

package com.upupor.service.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.upupor.service.dao.entity.Collect;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 收藏服务Mapper
 *
 * @author runkangyang (cruise)
 * @date 2020.01.27 15:19
 */
public interface CollectMapper extends BaseMapper<Collect> {

    /**
     * 只是根据用户id获取 (没有判定状态)
     *
     * @param userId
     * @return
     */
    List<Collect> listByUserId(@Param("userId") String userId);

    List<Collect> listByUserIdManage(@Param("userId") String userId);



    int existsCollectContent(@Param("contentId") String contentId, @Param("userId") String userId);

    int getByContentIdAndCollectStatus(@Param("contentId") String contentId, @Param("collectStatus") Integer collectStatus, @Param("userId") String userId);

    Collect getCollect(@Param("contentId") String contentId, @Param("collectStatus") Integer collectStatus, @Param("userId") String userId);

    Integer collectNum(@Param("collectType") Integer collectType, @Param("collectValue") String collectValue);
}
