package com.upupor.framework.config;

import lombok.Data;

/**
 * @author cruise
 * @createTime 2022-01-19 11:59
 */
@Data
public class Email {
    private Integer onOff;
    private String senderNickName;
    private String senderAccountName;
    private String accessKeyId;
    private String accessKeySecret;
}
