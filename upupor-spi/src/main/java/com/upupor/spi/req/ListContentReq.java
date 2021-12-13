package com.upupor.spi.req;

import lombok.Data;

import java.util.List;

/**
 * @author: YangRunkang(cruise)
 * @created: 2019/12/22 16:45
 */
@Data
public class ListContentReq {

    private String contentId;

    private String userId;

    private String title;

    private String shortContent;

    private Integer type;

    private Integer tagType;

    private Integer status;

    private Integer pageNum;

    private Integer pageSize;

    private Long createTime;

    /**
     * 个人中心管理页面-搜索标题
     */
    private String searchTitle;

    private String select;

    /**
     * 导航栏-搜索标题
     */
    private String navbarSearch;

    /**
     * 标签搜索
     */
    private List<String> tagIdList;

}
