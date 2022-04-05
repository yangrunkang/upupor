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

package com.upupor.service.listener;

import com.upupor.lucene.LuceneEvent;
import com.upupor.lucene.AbstractFlush;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 刷新Lucene
 *
 * @author Yang Runkang (cruise)
 * @date 2022年04月04日 13:50
 * @email: yangrunkang53@gmail.com
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class LuceneListener {

    private final List<AbstractFlush> abstractFlushList;

    @EventListener
    @Async
    public void flushLucene(LuceneEvent luceneEvent) {
        if (StringUtils.isEmpty(luceneEvent.getTargetId())) {
            return;
        }

        for (AbstractFlush abstractFlush : abstractFlushList) {
            if (abstractFlush.runDataType().equals(luceneEvent.getDataType())) {
                abstractFlush.flush(luceneEvent);
                break;
            }
        }
    }


}
