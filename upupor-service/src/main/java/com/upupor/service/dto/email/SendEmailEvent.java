package com.upupor.service.dto.email;

import lombok.Data;

/**
 * 邮件Dto
 *
 * @author YangRunkang(cruise)
 * @date 2020/02/06 19:12
 */
@Data
public class SendEmailEvent {

    private String title;

    private String content;

    private String toAddress;

}
