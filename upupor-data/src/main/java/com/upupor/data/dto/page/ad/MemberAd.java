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

package com.upupor.data.dto.page.ad;

import com.upupor.data.dao.entity.Member;
import com.upupor.data.dao.entity.converter.Converter;
import com.upupor.data.dao.entity.enhance.MemberEnhance;
import com.upupor.framework.CcConstant;

import java.util.List;

/**
 * @author Yang Runkang (cruise)
 * @date 2021年12月29日 20:48
 * @email: yangrunkang53@gmail.com
 */
public class MemberAd extends AbstractAd<MemberEnhance> {

    public MemberAd(List<MemberEnhance> memberEnhanceList) {
        super(memberEnhanceList);
    }

    @Override
    protected Boolean exists() {
        return getVoList().parallelStream().anyMatch(t -> t.getMember().getUserId().equals(CcConstant.GoogleAd.FEED_AD));
    }

    @Override
    protected void insertAd(int adIndex) {
        Member member = new Member();
        member.setUserId(CcConstant.GoogleAd.FEED_AD);
        getVoList().add(adIndex, Converter.memberEnhance(member));
    }
}
