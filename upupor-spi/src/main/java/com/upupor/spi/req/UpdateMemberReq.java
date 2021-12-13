package com.upupor.spi.req;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author: YangRunkang(cruise)
 * @created: 2019/12/28 14:36
 */
@Data
public class UpdateMemberReq {

    private String userId;

    private String userName;

    private String email;

    private String phone;

    /**
     * member extend
     */
    private String birthday;

//    private Integer age;

    private String introduce;

    @Deprecated
    private MultipartFile bgImg;

    private Integer openEmail;
}
