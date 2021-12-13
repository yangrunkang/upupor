package com.upupor.service.dto.page;

import com.upupor.framework.utils.CcDateUtil;
import com.upupor.service.dao.entity.Content;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 全局Dto
 *
 * @author YangRunkang(cruise)
 * @date 2020/01/31 21:07
 */
@Data
public class GlobalIndexDto {

    /**
     * 全局推荐文章
     */
    private List<Content> globalContentList;

    private String createDate;


    public GlobalIndexDto() {
        this.globalContentList = new ArrayList<>();
        this.createDate = CcDateUtil.timeStamp2Date(CcDateUtil.getCurrentTime());
    }

}
