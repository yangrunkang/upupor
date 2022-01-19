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

package com.upupor.service.business.ad;

import com.upupor.service.business.aggregation.dao.entity.Content;
import com.upupor.framework.CcConstant;

import java.util.List;

/**
 * @author Yang Runkang (cruise)
 * @date 2021年12月29日 20:51
 * @email: yangrunkang53@gmail.com
 */
public class ContentAd extends AbstractAd<Content>{
    public ContentAd(List<Content> contents) {
        super(contents);
    }

    @Override
    protected Boolean exists() {
        return getVoList().parallelStream().anyMatch(t -> t.getContentId().equals(CcConstant.GoogleAd.FEED_AD));
    }

    @Override
    protected void insertAd(int adIndex) {
        Content adContent = Content.create();
        adContent.setContentId(CcConstant.GoogleAd.FEED_AD);
        getVoList().add(adIndex, adContent);
    }
}
