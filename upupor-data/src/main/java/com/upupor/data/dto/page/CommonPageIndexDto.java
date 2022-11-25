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

package com.upupor.data.dto.page;

import com.upupor.data.dao.entity.Content;
import com.upupor.data.dao.entity.Member;
import com.upupor.data.dao.entity.Tag;
import com.upupor.data.dto.page.ad.AbstractAd;
import com.upupor.data.dto.page.common.ListBannerDto;
import com.upupor.data.dto.page.common.ListContentDto;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 公共页面(各个菜单的首页)
 *
 * @author YangRunkang(cruise)
 * @date 2020/05/14 22:36
 */
@Data
public class CommonPageIndexDto {

    /**
     * 默认加载广告
     */
    private Boolean isLoadAd = Boolean.TRUE;


    /**
     * 左边的栏目
     */
    private List<Tag> tagList;

    /**
     * 文章列表
     */
    private ListContentDto listContentDto;

    /**
     * 当前根url
     */
    private String currentRootUrl;

    /**
     * 活跃用户
     */
    private List<Member> memberList;

    /**
     * 最近一周新增文章
     */
    private List<Content> latestContentList;

    /**
     * 是否领取过今天积分
     */
    private Boolean isGetDailyPoints;

    /**
     * Banner(推荐)
     */
    private ListBannerDto listBannerDto;

    /**
     * 根据文章类型来创建内容
     */
    private HrefDesc createContentDesc;

    public CommonPageIndexDto() {
        init();
    }

    private void init() {
        this.listContentDto = new ListContentDto();
        this.tagList = new ArrayList<>();
        this.latestContentList = new ArrayList<>();
    }

    public CommonPageIndexDto(Boolean isLoadAd) {
        init();
        this.isLoadAd = isLoadAd;
    }

    public ListContentDto getListContentDto() {
        if (this.isLoadAd) {
            AbstractAd.ad(listContentDto.getContentList());
        }
        return listContentDto;
    }

}
