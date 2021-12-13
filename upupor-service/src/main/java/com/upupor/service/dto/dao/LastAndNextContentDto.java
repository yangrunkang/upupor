package com.upupor.service.dto.dao;

import lombok.Data;

/**
 * 上一篇及下一篇文章
 *
 * @author YangRunkang(cruise)
 * @date 2020/11/15 01:15
 */
@Data
public class LastAndNextContentDto {
    private ContentIdAndTitle nextContent;
    private ContentIdAndTitle lastContent;
}
