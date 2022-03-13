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

package com.upupor.service.scheduled.sitemap;

import com.upupor.service.dto.seo.GoogleSeoDto;
import lombok.Getter;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author Yang Runkang (cruise)
 * @date 2022年03月13日 21:24
 * @email: yangrunkang53@gmail.com
 */
public abstract class AbstractSiteMap<T> {

    @Getter
    List<GoogleSeoDto> googleSeoDtoList = new ArrayList<>();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    /**
     * 检查数据
     * @return
     */
    protected abstract Boolean dataCheck();


    protected abstract List<T> getSiteMapData();

    /**
     * 渲染SiteMap
     */
    protected abstract void renderSiteMap(List<T> tList);


    public void doBusiness(){
        if(dataCheck()){
            List<T> siteMapDataList = getSiteMapData();
            if(CollectionUtils.isEmpty(siteMapDataList)){
                return;
            }
            renderSiteMap(siteMapDataList);
        }
    }

}
