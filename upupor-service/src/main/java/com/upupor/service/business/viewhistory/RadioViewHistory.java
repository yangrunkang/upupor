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

package com.upupor.service.business.viewhistory;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.upupor.service.dao.entity.Radio;
import com.upupor.service.dao.entity.ViewHistory;
import com.upupor.service.dao.mapper.RadioMapper;
import com.upupor.service.types.ViewTargetType;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author cruise
 * @createTime 2021-12-28 18:49
 */
@Component
public class RadioViewHistory extends AbstractViewHistory<Radio> {

    @Resource
    private RadioMapper radioMapper;


    @Override
    public ViewTargetType viewTargetType() {
        return ViewTargetType.RADIO;
    }

    @Override
    public void setViewHistoryTitleAndUrl() {
        for (ViewHistory viewHistory : getSpecifyViewHistory()) {
            for (Radio radio : getTargetList()) {
                if (radio.getRadioId().equals(viewHistory.getTargetId())) {
                    viewHistory.setTitle(radio.getRadioIntro());
                    viewHistory.setUrl("/r/" + radio.getRadioId());
                    viewHistory.setSource(viewTargetType().getName());
                }
            }
        }
    }

    @Override
    public List<Radio> getTargetList() {
        LambdaQueryWrapper<Radio> query = new LambdaQueryWrapper<Radio>()
                .in(Radio::getRadioId, getViewHistoryTargetIdList());
        return radioMapper.selectList(query);
    }

}
