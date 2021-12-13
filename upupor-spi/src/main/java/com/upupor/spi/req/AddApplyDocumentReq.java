package com.upupor.spi.req;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * 添加申请材料
 *
 * @author YangRunkang(cruise)
 * @date 2020/01/26 11:52
 */
@Data
public class AddApplyDocumentReq {
    private String applyId;
    //    private String adImgUrl;
    private String upload;
    private String applyAdText;
    private MultipartFile file;
}
