package com.upupor.service.luence;

import com.upupor.service.luence.abstracts.AbstractLuenceService;
import com.upupor.service.luence.dto.ContentDto;
import lombok.RequiredArgsConstructor;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Luence全文检索服务
 *
 * @author YangRunkang(cruise)
 * @date 2021/06/11 22:32
 */
@Service
@RequiredArgsConstructor
public class LuenceService extends AbstractLuenceService<ContentDto> {

    /**
     * 索引位置
     */
//    @Value("${codingvcr.luence.indexDic:null}")
    private final String luenceIndexDic = "/Users/runkangyang/idea/GitHub/demos/data";

    public static void main(String[] args) {
        ContentDto contentDto = new ContentDto();
        contentDto.setContentId("13213");
        contentDto.setTitleIntroContent("akhsdiofhnlmsa;");
        // 新增
        new LuenceService().save(contentDto);


        ContentDto query = new ContentDto();
        query.setContentId("akhsdiofhnlmsa");
        List<ContentDto> queryResult = new LuenceService().query(query);
        System.out.println(queryResult);
    }

    /**
     * 保存
     *
     * @param contentDto
     */
    @Override
    public void save(ContentDto contentDto) {


        try {
            //指定索引库存放的路径
            Directory directory = FSDirectory.open(new File(luenceIndexDic).toPath());
            //索引库还可以存放到内存中
            //Directory directory = new RAMDirectory();
            //创建indexwriterCofig对象
            IndexWriterConfig config = new IndexWriterConfig();
            //创建indexwriter对象
            IndexWriter indexWriter = new IndexWriter(directory, config);

            //创建文件名域
            // 这里创建具体的业务对象模型
            Field contentId = new TextField("contentId", contentDto.getContentId(), Field.Store.YES);
            Field titleIntroContent = new TextField("titleIntroContent", contentDto.getTitleIntroContent(), Field.Store.YES);

            //创建document对象
            Document document = new Document();
            document.add(contentId);
            document.add(titleIntroContent);

            //创建索引，并写入索引库
            indexWriter.addDocument(document);

            //关闭indexwriter
            indexWriter.close();
        } catch (Exception e) {

        }

    }

    /**
     * 删除
     *
     * @param contentDto
     */
    @Override
    public void delete(ContentDto contentDto) {

    }

    /**
     * 查询
     *
     * @param contentDto
     */
    @Override
    public List<ContentDto> query(ContentDto contentDto) {
        List<ContentDto> contentDtoList = new ArrayList<>();
        try {
            //指定索引库存放的路径
            Directory directory = FSDirectory.open(new File(luenceIndexDic).toPath());
            //创建indexReader对象
            IndexReader indexReader = DirectoryReader.open(directory);
            //创建indexsearcher对象
            IndexSearcher indexSearcher = new IndexSearcher(indexReader);
            //创建查询
            Query query = new TermQuery(
                    new Term("titleIntroContent", contentDto.getTitleIntroContent())
            );


            //执行查询
            //第一个参数是查询对象，第二个参数是查询结果返回的最大记录数
            TopDocs topDocs = indexSearcher.search(query, 10);

            //查询结果的总条数
//            System.out.println("查询结果的总条数："+ topDocs.totalHits);
            //遍历查询结果

            //topDocs.scoreDocs存储了document对象的id
            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                //scoreDoc.doc属性就是document对象的id
                //根据document的id找到document对象
                Document document = indexSearcher.doc(scoreDoc.doc);

                ContentDto queryResult = new ContentDto();
                queryResult.setContentId(document.get("contentId"));
                queryResult.setTitleIntroContent(document.get("titleIntroContent"));

                contentDtoList.add(queryResult);
            }
            //关闭indexreader对象
            indexReader.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return contentDtoList;
    }

}
