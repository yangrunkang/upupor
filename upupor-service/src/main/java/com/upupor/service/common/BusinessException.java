package com.upupor.service.common;

/**
 * @author: YangRunkang(cruise)
 * @created: 2019/12/20 03:02
 */
public class BusinessException extends RuntimeException {

    private final Integer code;
    private String message;


    public BusinessException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public BusinessException(ErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    /**
     * @param errorCode
     * @param appendMessage 是追加的异常信息
     */
    public BusinessException(ErrorCode errorCode, String appendMessage) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage() + CcConstant.COMMA + appendMessage;
    }

    /**
     * @param errorCode
     * @param appendMessage 是追加的异常信息,但是移除 都好
     */
    public BusinessException(ErrorCode errorCode, String appendMessage, Boolean isAddComma) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage() + CcConstant.COMMA + appendMessage;
        if (!isAddComma) {
            this.message = errorCode.getMessage() + appendMessage;
        }
    }

    public Integer getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public String toString() {
        return String.format("【%d】 %s", this.code, this.message);
    }
}
