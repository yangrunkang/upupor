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

package com.upupor.lucene.core;

import com.upupor.lucene.AbstractIndexDto;
import com.upupor.lucene.AbstractLucene;
import lombok.Data;
import org.apache.lucene.document.Field;

import java.util.List;

/**
 * Lucene索引服务
 *
 * @author Yang Runkang (cruise)
 * @date 2022年03月27日 10:02
 * @email: yangrunkang53@gmail.com
 */
@Data
public class LuceneIndexService extends AbstractLucene {

    @Override
    protected Boolean addIndex(AbstractIndexDto addIndexDto) {
        return super.addIndex(addIndexDto);
    }

    @Override
    protected List<AbstractIndexDto> list(AbstractIndexDto queryIndexDto) {
        return null;
    }

    @Override
    protected Boolean updateIndex(AbstractIndexDto updateIndexDto) {
        return super.updateIndex(updateIndexDto);
    }

    @Override
    protected Boolean deleteIndex(AbstractIndexDto deleteIndexDto) {
        return super.deleteIndex(deleteIndexDto);
    }
}
