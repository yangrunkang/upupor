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

package com.upupor.service.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.upupor.service.dao.entity.Attention;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AttentionMapper extends BaseMapper<Attention> {

    /**
     * 检查是否存在重复关注
     *
     * @param attentionUserId
     * @param userId
     * @return
     */
    int checkExists(@Param("attentionUserId") String attentionUserId, @Param("userId") String userId);



    int getAttentionNum(@Param("userId") String userId);


    /**
     * 获取关注者
     *
     * @param userId
     * @return
     */
    List<Attention> getAttentions(@Param("userId") String userId);
}
