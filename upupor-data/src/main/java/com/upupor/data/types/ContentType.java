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

package com.upupor.data.types;

/**
 * @author cruise
 * @createTime 2021-12-31 18:03
 */

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

//import static com.upupor.framework.CcConstant.OSS_URL;


/**
 * Note: 如果有新增,记得这个地方需要修改: com.upupor.web.controller.CommentsController#add(com.upupor.data.dto.req.AddCommentReq)
 */
@Getter
public enum ContentType {


    /**
     * 技术
     */
    TECH(1, "技术", "/tech", "发布技术文章", "/icons/write/write-tech.png", "创建 > 技术"),

    /**
     * 问答
     */
    QA(2, "问答", "/qa", "我要提问", "/icons/write/write-qa.png", "创建 > 问答"),

    /**
     * 分享
     */
    SHARE(3, "分享", "/share", "我要分享", "/icons/write/write-share.png", "创建 > 分享"),

    /**
     * 职场
     */
//    WORKPLACE(4, "职场", "/workplace", "分享职场新鲜事", "/icons/write/write-work.png", "创建 > 职场"),

    /**
     * 记录
     */
//    RECORD(5, "记录", "/record", "记录美好生活", "/icons/write/write-record.png", "创建 > 记录"),

    /**
     * 短内容
     */
//    TOPIC(6, "短内容", "/topic", "发布短内容", "/icons/system/topic.png", "创建 > 短内容"),

    /**
     * 留言
     */
    MESSAGE(7, "留言"),

    /**
     * 电台
     */
    RADIO(8, "电台"),
    /**
     * GPT
     */
    GPT(9, "GPT", "/gpt", "GPT", "/icons/write/ChatGPT.svg", "创建 > GPT");
    @EnumValue
    private final Integer type;
    private final String name;
    private final String url;
    private final String webText;
    private final String icon;
    private final String tips;

    ContentType(Integer type, String name, String url, String webText, String icon, String tips) {
        this.type = type;
        this.name = name;
        this.url = url;
        this.webText = webText;
        this.icon = icon;
        this.tips = tips;
    }

    ContentType(Integer type, String name) {
        this.type = type;
        this.name = name;
        this.url = null;
        this.webText = null;
        this.icon = null;
        this.tips = null;
    }

    public static String getUrl(ContentType contentType) {
        if (Objects.isNull(contentType)) {
            return null;
        }
        ContentType[] values = values();
        for (ContentType tmp : values) {
            if (tmp.equals(contentType)) {
                return tmp.getUrl();
            }
        }
        return null;
    }

    public static String getName(ContentType contentType) {
        if (Objects.isNull(contentType)) {
            return null;
        }
        ContentType[] values = values();
        for (ContentType tmp : values) {
            if (tmp.equals(contentType)) {
                return tmp.getName();
            }
        }
        return null;
    }


    public static ContentType getByStartUrl(String url) {
        for (ContentType contentType : values()) {
            if (StringUtils.isEmpty(contentType.getUrl())) {
                continue;
            }

            if (url.startsWith(contentType.getUrl())) {
                return contentType;
            }
        }
        return null;
    }


    public static ContentType getByContentType(ContentType contentType) {
        if (Objects.isNull(contentType)) {
            return null;
        }
        ContentType[] values = values();
        for (ContentType tmp : values) {
            if (tmp.equals(contentType)) {
                return tmp;
            }
        }
        return null;
    }

    /**
     * 内容资源
     *
     * @return
     */
    public static List<ContentType> contentSource() {
        List<ContentType> contentTypeList = new ArrayList<>();
        contentTypeList.add(TECH);
        contentTypeList.add(QA);
        contentTypeList.add(SHARE);
        contentTypeList.add(GPT);
//        contentTypeList.add(RECORD);
//        contentTypeList.add(WORKPLACE);
//        contentTypeList.add(TOPIC);
        return contentTypeList;
    }


    /**
     * 是否是文章
     *
     * @return
     */
    public static Boolean isContent(ContentType contentType) {
        return contentSource().contains(contentType);
    }

    /**
     * 是否是留言板消息
     *
     * @return
     */
    public static Boolean isMessagetBoard(ContentType contentType) {
        return ContentType.MESSAGE.equals(contentType);
    }

    /**
     * 是否是电台
     *
     * @return
     */
    public static Boolean isRadio(ContentType contentType) {
        return ContentType.RADIO.equals(contentType);
    }


    /**
     * 获取内容描述
     *
     * @param contentType
     * @return
     */
    public String getContentTypeDesc(ContentType contentType) {
        ContentType byContentType = ContentType.getByContentType(contentType);
        if (Objects.isNull(byContentType)) {
            return "内容";
        }
        return byContentType.getName();
    }


}