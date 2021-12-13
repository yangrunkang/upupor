package com.upupor.spi.req;

import lombok.Data;

/**
 * 添加音频
 *
 * @author YangRunkang(cruise)
 * @date 2020/11/15 19:10
 */
@Data
public class AddRadioReq {

    private String radioIntro;

    private String fileUrl;

}
