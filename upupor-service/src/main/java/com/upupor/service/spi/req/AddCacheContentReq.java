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

package com.upupor.service.spi.req;

import com.upupor.service.types.ContentType;
import com.upupor.service.types.OriginType;
import lombok.Data;

/**
 * 添加缓存文章请求
 * <p>
 * 属性不要轻易删除,因为缓存回显有用到
 *
 * @author YangRunkang(cruise)
 * @date 2020/04/19 17:36
 */
@Data
public class AddCacheContentReq {

    private String title;
    private String content;
    private String mdContent;

    private String shortContent;

    /**
     * 文章类型
     */
    private ContentType contentType;

    private String tagIds;

    private Boolean edit;

    // 如果为空也没事,反正前端按钮都不会显示
    private String userId;
    private String contentId;

    //
    private OriginType originType;
    private String noneOriginLink;

}
