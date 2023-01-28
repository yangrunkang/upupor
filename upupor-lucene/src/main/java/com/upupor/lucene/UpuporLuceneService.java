/*
 * <!--
 *   ~ MIT License
 *   ~
 *   ~ Copyright (c) 2021-2022 yangrunkang
 *   ~
 *   ~ Author: yangrunkang
 *   ~ Email: yangrunkang53@gmail.com
 *   ~
 *   ~ Permission is hereby granted, free of charge, to any person obtaining a copy
 *   ~ of this software and associated documentation files (the "Software"), to deal
 *   ~ in the Software without restriction, including without limitation the rights
 *   ~ to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *   ~ copies of the Software, and to permit persons to whom the Software is
 *   ~ furnished to do so, subject to the following conditions:
 *   ~
 *   ~ The above copyright notice and this permission notice shall be included in all
 *   ~ copies or substantial portions of the Software.
 *   ~
 *   ~ THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   ~ IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *   ~ FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *   ~ AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *   ~ LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *   ~ OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *   ~ SOFTWARE.
 *   -->
 */

package com.upupor.lucene;

import com.upupor.lucene.bean.LuceneBean;
import com.upupor.lucene.dto.LuceneQueryResultDto;
import com.upupor.lucene.enums.LuceneDataType;
import com.upupor.lucene.enums.SearchType;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.upupor.lucene.dto.ContentFieldAndSearchDto.*;

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

    /**
     * 检索
     *
     * @param searchField 检索字段
     * @param keyword     关键字
     * @param searchType  检索类型
     * @return
     */
    public LuceneQueryResultDto searchTitle(String searchField, String keyword, SearchType searchType) {
        IndexReader reader = null;
        try {
            Query query;
            if (searchType.equals(SearchType.LIKE)) {
                query = new WildcardQuery(new Term(searchField, "*" + keyword.toLowerCase() + "*")); // 模糊查询
            } else if (searchType.equals(SearchType.EXACT)) {
                query = new QueryParser(searchField, LuceneBean.analyzer).parse(keyword); // 精确查询
            } else {
                throw new RuntimeException("请输入检索类型");
            }

            int hitsPerPage = 3000; // 默认3000
            reader = DirectoryReader.open(indexWriter);
            IndexSearcher searcher = new IndexSearcher(reader);
            TopDocs docs = searcher.search(query, hitsPerPage);
            // 命中结果
            ScoreDoc[] hits = docs.scoreDocs;
            LuceneQueryResultDto luceneQueryResultDto = new LuceneQueryResultDto();
            if (ArrayUtils.isEmpty(hits)) {
                return luceneQueryResultDto;
            }

            List<LuceneQueryResultDto.Data> resultList = new ArrayList<>();
            for (ScoreDoc hit : hits) {
                int docId = hit.doc;
                Document d = searcher.doc(docId);
                resultList.add(LuceneQueryResultDto.Data.builder()
                        .title(d.get(TITLE))
                        .target(d.get(TARGET_ID))
                        .luceneDataType(LuceneDataType.getByEnumStr(d.get(LUCENE_DATA_TYPE)))
                        .build());
            }

            luceneQueryResultDto.setTotal((long) hits.length);
            luceneQueryResultDto.setResultList(resultList);
            return luceneQueryResultDto;

        } catch (Exception e) {
            throw new RuntimeException("查询失败", e);
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
     * 删除
     *
     * @param targetId
     */
    public void deleteDocumentByTargetId(String targetId) {
        try {
            Query query = new QueryParser(TARGET_ID, LuceneBean.analyzer).parse(targetId); // 精确查询
            indexWriter.deleteDocuments(query);
        } catch (Exception e) {
            throw new RuntimeException("删除文档失败");
        }
    }

    /**
     * 添加文档
     *
     * @param title    文章标题
     * @param targetId 文章Id
     */
    public void addDocument(String title, String targetId, LuceneDataType luceneDataType) {
        if (StringUtils.isEmpty(title)) {
            throw new RuntimeException("标题为空,不能创建索引");
        }

        if (Objects.isNull(indexWriter)) {
            throw new RuntimeException("系统暂时索引");
        }

        if (!indexWriter.isOpen()) {
            throw new RuntimeException("系统索引未开启");
        }

        try {
            Document doc = new Document();
            doc.add(new StringField(TITLE, title.toLowerCase(), Field.Store.YES));
            doc.add(new StringField(TARGET_ID, targetId, Field.Store.YES));
            doc.add(new StringField(LUCENE_DATA_TYPE, luceneDataType.toString(), Field.Store.YES));
            indexWriter.addDocument(doc);
        } catch (Exception e) {
            throw new RuntimeException("添加文档异常");
        } /*finally {
            // 添加完后关闭
            try {
                indexWriter.close();
            } catch (IOException ignored) {
            }
        }*/

    }

}
