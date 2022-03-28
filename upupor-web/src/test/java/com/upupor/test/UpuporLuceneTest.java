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

package com.upupor.test;

import com.upupor.lucene.SearchType;
import com.upupor.lucene.UpuporLuceneService;
import com.upupor.lucene.dto.LucenuQueryResultDto;
import com.upupor.web.UpuporWebApplication;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static com.upupor.lucene.dto.ContentFieldAndSearchDto.CONTENT_ID;
import static com.upupor.lucene.dto.ContentFieldAndSearchDto.TITLE;

/**
 * @author Yang Runkang (cruise)
 * @date 2022年03月27日 23:47
 * @email: yangrunkang53@gmail.com
 */

@SpringBootTest(classes = UpuporWebApplication.class)
@RequiredArgsConstructor
public class UpuporLuceneTest {

    @Resource
    private UpuporLuceneService upuporLuceneService;


    @Test
    public void testInit() {
        upuporLuceneService.addDocument("asdf", "ff");
        upuporLuceneService.addDocument("123", "ff123");
        upuporLuceneService.addDocument("2r", "rrr");

//        LucenuQueryResultDto search1 = upuporLuceneService.search("123");// 精确匹配
        LucenuQueryResultDto search = upuporLuceneService.searchTitle(TITLE, "*2*", SearchType.LIKE);// 模糊搜索-支持后缀模糊查询
//        LucenuQueryResultDto search = upuporLuceneService.search("2");// 模糊搜索-支持后缀模糊查询
        System.out.println();
        System.out.println();

    }

    @Test
    public void testDelete() {
        upuporLuceneService.addDocument("asdf", "ff");
        upuporLuceneService.addDocument("123", "ff123");
        upuporLuceneService.addDocument("2r", "rrr");

        LucenuQueryResultDto search = upuporLuceneService.searchTitle(CONTENT_ID, "ff123", SearchType.EXACT);// 模糊搜索-支持后缀模糊查询

        search.getResultList().forEach(result -> {
            upuporLuceneService.deleteDocumentByContentId("ff123");
        });
        LucenuQueryResultDto search2 = upuporLuceneService.searchTitle(CONTENT_ID, "ff123", SearchType.EXACT);// 模糊搜索-支持后缀模糊查询
        System.out.println();

    }

}
