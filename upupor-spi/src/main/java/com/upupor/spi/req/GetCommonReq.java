package com.upupor.spi.req;

import lombok.Data;

/**
 * 获取公共内容Req
 *
 * @author YangRunkang(cruise)
 * @date 2020/05/14 22:40
 */
@Data
public class GetCommonReq {

    private String tagId;

    private String tagInId;

    private Integer pageNum;

    private Integer pageSize;

    /**
     * 获取指定类型的文章
     */
    private Integer contentType;

    public static GetCommonReq create(String tagId, String tagInId, Integer pageNum, Integer pageSize, Integer contentType) {
        GetCommonReq getCommonReq = new GetCommonReq();
        getCommonReq.setTagId(tagId);
        getCommonReq.setTagInId(tagInId);
        getCommonReq.setPageNum(pageNum);
        getCommonReq.setPageSize(pageSize);
        getCommonReq.setContentType(contentType);
        return getCommonReq;

    }
}
