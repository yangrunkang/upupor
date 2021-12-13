package com.upupor.service.dto.seo;

import lombok.Data;

/**
 * Google Seo 数据对象
 *
 * @author YangRunkang(cruise)
 * @date 2020/03/12 03:55
 */
@Data
public class GoogleSeoDto {
    private String loc;
    private String lastmod;
    private String changeFreq;
    private String priority;
}
