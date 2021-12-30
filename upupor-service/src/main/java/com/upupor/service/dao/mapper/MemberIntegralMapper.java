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
import com.upupor.service.dao.entity.MemberIntegral;
import com.upupor.spi.req.GetMemberIntegralReq;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MemberIntegralMapper extends BaseMapper<MemberIntegral> {

    /**
     * 根据条件检索数目
     *
     * @param getMemberIntegralReq
     * @return
     */
    Integer countByCondition(GetMemberIntegralReq getMemberIntegralReq);

    Integer getTotalUserIntegral(@Param("userId") String userId);

    /**
     * 每日签到的用户Id集合
     *
     * @param startTime
     * @param endTime
     * @return
     */
    List<String> dailyPointsUserIdList(@Param("startTime") Long startTime, @Param("endTime") Long endTime);

}
