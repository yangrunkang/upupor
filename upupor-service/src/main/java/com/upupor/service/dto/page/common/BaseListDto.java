package com.upupor.service.dto.page.common;

import com.github.pagehelper.PageInfo;
import com.upupor.service.utils.PageUtils;
import lombok.Data;

/**
 * List列表基类
 *
 * @author: Yang Runkang(cruise)
 * @email: yangrunkang53@gmail.com
 * @create: 2021-12-09 00:24
 */
@Data
public class BaseListDto {
    private Long total = 0L;
    private String paginationHtml;

    public BaseListDto() {
    }

    public BaseListDto(PageInfo pageInfo) {
        this.total = pageInfo.getTotal();
        this.paginationHtml = PageUtils.paginationHtml(pageInfo);
    }
}
