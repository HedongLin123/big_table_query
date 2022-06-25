package com.itdl.exception;

import lombok.Getter;

@Getter
public class BizException extends RuntimeException {

    private final String code;
    private final String message;

    public BizException(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
