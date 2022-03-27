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

import com.upupor.lucene.bean.LuceneBean;
import com.upupor.lucene.dto.LucenuQueryResultDto;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 抽象全文检索
 *
 * @author Yang Runkang (cruise)
 * @date 2022年03月27日 01:53
 * @email: yangrunkang53@gmail.com
 */
@Service
public class UpuporLuceneService {

    @Resource
    private IndexWriter indexWriter;

    private static final String TITLE = "title";
    private static final String CONTENT_ID = "content_id";

    /**
     * 检索
     * @param title
     * @return
     */
    public LucenuQueryResultDto search(String title) {
        LucenuQueryResultDto lucenuQueryResultDto = new LucenuQueryResultDto();

        IndexReader reader = null;
        try {
            Query query = new QueryParser(TITLE, LuceneBean.analyzer).parse(title);

            int hitsPerPage = 300; // 默认300
            reader = DirectoryReader.open(indexWriter);
            IndexSearcher searcher = new IndexSearcher(reader);
            TopDocs docs = searcher.search(query, hitsPerPage);
            // 命中结果
            ScoreDoc[] hits = docs.scoreDocs;

            if (ArrayUtils.isEmpty(hits)) {
                return lucenuQueryResultDto;
            }

            List<LucenuQueryResultDto.Data> resultList = new ArrayList<>();
            for (ScoreDoc hit : hits) {
                int docId = hit.doc;
                Document d = searcher.doc(docId);
                resultList.add(LucenuQueryResultDto.Data.builder()
                        .title(d.get(TITLE))
                        .contentId(d.get(CONTENT_ID))
                        .build());
            }

            lucenuQueryResultDto.setTotal((long) hits.length);
            lucenuQueryResultDto.setResultList(resultList);
            return lucenuQueryResultDto;

        } catch (Exception e) {
            throw new RuntimeException("查询失败",e);
        } finally {
            try {
                if (Objects.nonNull(reader)) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 添加文档
     *
     * @param title
     * @param contentId
     */
    public void addDocument(String title, String contentId) {
        if (Objects.isNull(indexWriter)) {
            throw new RuntimeException("系统暂时索引");
        }

        if (!indexWriter.isOpen()) {
            throw new RuntimeException("系统未开启");
        }

        try {
            Document doc = new Document();
            doc.add(new StringField(TITLE, title, Field.Store.YES));
            doc.add(new StringField(CONTENT_ID, contentId, Field.Store.YES));
            indexWriter.addDocument(doc);
        } catch (Exception e) {
            throw new RuntimeException("添加文档异常");
        }/*finally {
            // 添加完后关闭
            indexWriter.close();
        }*/

    }

}
