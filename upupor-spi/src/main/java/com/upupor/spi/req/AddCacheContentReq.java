package com.upupor.spi.req;

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

    private String shortContent;

    /**
     * 文章类型
     */
    private Integer contentType;

    private String tagIds;

    private Boolean edit;

    // 如果为空也没事,反正前端按钮都不会显示
    private String userId;
    private String contentId;

    //
    private Integer originType;
    private String noneOriginLink;

}
