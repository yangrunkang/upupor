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

package com.upupor.lucene;

import com.upupor.lucene.enums.LuceneDataType;
import com.upupor.lucene.enums.LuceneOperationType;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;

import javax.annotation.Resource;

/**
 * 抽象刷新Lucene全文索引业务逻辑
 *
 * @author Yang Runkang (cruise)
 * @date 2022年04月04日 14:13
 * @email: yangrunkang53@gmail.com
 */
@Slf4j
public abstract class AbstractFlush<T> {

    @Getter
    protected LuceneEvent event;

    @Getter
    @Resource
    protected UpuporLuceneService upuporLuceneService;

    /**
     * 添加索引
     */
    protected abstract void add();

    /**
     * 更新索引,要注意状态,例如文章可以变更为删除
     */
    protected void update() {
        delete();
        add();
    }

    /**
     * 删除索引
     */
    protected abstract void delete();

    /**
     * 初始化目标对象
     */
    protected abstract void initTargetObject();

    /**
     * 处理的数据类型
     *
     * @return
     */
    public abstract LuceneDataType runDataType();

    public void flush(LuceneEvent event) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        // 初始化抽象类基础参数
        this.event = event;
        // 初始化目标对象
        this.initTargetObject();
        // 刷新Lucene具体操作
        if (LuceneOperationType.ADD.equals(this.event.getOperationType())) {
            add();
        }

        if (LuceneOperationType.UPDATE.equals(this.event.getOperationType())) {
            update();
        }

        if (LuceneOperationType.DELETE.equals(this.event.getOperationType())) {
            delete();
        }

        stopWatch.stop();
        log.info("[FlushLucene][操作类型:{}][数据类型:{}][目标Id:{}][耗时:{}ms]",event.getOperationType(),event.getDataType(),event.getTargetId(),stopWatch.getTime());
    }

}
