package com.upupor.service.common;

import lombok.Data;

import java.io.Serializable;

/**
 * UPUPOR响应对象
 *
 * @author: YangRunkang(cruise)
 * @created: 2019/12/20 03:00
 */
@Data
public class CcResponse implements Serializable {

    /**
     * 响应码
     */
    private Integer code = 0;

    /**
     * 响应对象
     */
    private Object data;


    public CcResponse(Integer code, Object data) {
        this.code = code;
        this.data = data;
    }

    public CcResponse() {
    }

    public CcResponse(BusinessException businessException) {
        this.code = businessException.getCode();
        this.data = businessException.getMessage();
    }

    public CcResponse(ErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.data = errorCode.getMessage();
    }
}
