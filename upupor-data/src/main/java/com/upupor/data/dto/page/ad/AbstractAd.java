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

import com.upupor.data.dao.entity.*;
import com.upupor.framework.CcConstant;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * 抽象广告
 *
 * @author Yang Runkang (cruise)
 * @date 2021年12月29日 20:44
 * @email: yangrunkang53@gmail.com
 */
public abstract class AbstractAd<T extends BaseEntity> {

    /**
     * vo列表
     */
    private final List<T> voList;

    public AbstractAd(List<T> voList) {
        this.voList = voList;
    }

    public List<T> getVoList() {
        return voList;
    }

    /**
     * 是否已经存在广告
     *
     * @return
     */
    protected abstract Boolean exists();

    /**
     * 插入广告
     *
     * @param adIndex
     */
    protected abstract void insertAd(int adIndex);


    /**
     * 广告
     */
    void ad() {
        // 个人主页文章列表添加广告
        if (!CollectionUtils.isEmpty(voList)) {
            if (!exists()) {
                int adIndex = new Random().nextInt(CcConstant.Page.SIZE);
                int maxIndex = voList.size() - 1;
                if (adIndex <= 2) {
                    adIndex = 3;
                }
                if (adIndex >= maxIndex) {
                    adIndex = maxIndex;
                }

                insertAd(adIndex);
            }
        }
    }


    public static void ad(List<?> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        Optional<?> first = list.stream().findFirst();
        if (first.isPresent()) {
            Object o = first.get();
            if (o instanceof Content) {
                new ContentAd((List<Content>) list).ad();
            } else if (o instanceof Comment) {
                new CommentAd((List<Comment>) list).ad();
            } else if (o instanceof Member) {
                new MemberAd((List<Member>) list).ad();
            } else if (o instanceof Radio) {
                new RadioAd((List<Radio>) list).ad();
            }
        }
    }

}