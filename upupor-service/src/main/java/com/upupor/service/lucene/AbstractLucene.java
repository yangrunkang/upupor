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

package com.upupor.service.lucene;

import java.util.List;

/**
 * 抽象全文检索
 *
 * @author Yang Runkang (cruise)
 * @date 2022年03月27日 01:53
 * @email: yangrunkang53@gmail.com
 */
public abstract class AbstractLucene {

    /**
     * 添加索引
     *
     * @param addIndexDto
     */
    protected Boolean addIndex(AbstractIndexDto addIndexDto) {
        return Boolean.FALSE;
    }

    /**
     * 查询索引
     *
     * @param queryIndexDto
     * @return
     */
    protected abstract List<AbstractIndexDto> list(AbstractIndexDto queryIndexDto);


    /**
     * 更新索引
     * @param updateIndexDto
     * @return
     */
    protected Boolean updateIndex(AbstractIndexDto updateIndexDto) {
        return Boolean.FALSE;
    }

    /**
     * 删除索引
     * @param deleteIndexDto
     * @return
     */
    protected Boolean deleteIndex(AbstractIndexDto deleteIndexDto) {
        return Boolean.FALSE;
    }

}
