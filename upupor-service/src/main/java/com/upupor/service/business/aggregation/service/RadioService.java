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

package com.upupor.service.business.aggregation.service;

import com.upupor.service.dao.entity.Radio;
import com.upupor.service.dto.page.common.ListRadioDto;

/**
 * 音频服务
 *
 * @author YangRunkang(cruise)
 * @date 2020/11/15 20:31
 */
public interface RadioService {

    Boolean addRadio(Radio radio);

    ListRadioDto listRadioByUserId(Integer pageNum, Integer pageSize, String userId, String searchTitle);

    Radio getByRadioId(String radioId);

    Radio getByRadioIdAndUserId(String radioId, String userId);

    Integer updateRadio(Radio radio);

    ListRadioDto list(Integer pageNum, Integer pageSize);

    Integer total();

    /**
     * 文章作者是否有电台
     *
     * @param userId
     */
    Boolean userHasRadio(String userId);

}
