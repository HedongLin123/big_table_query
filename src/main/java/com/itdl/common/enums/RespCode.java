package com.itdl.common.enums;

import lombok.Getter;

@Getter
public enum RespCode {

    SUCCESS("000000", "success"),
    REQUEST_PARAM_ERROR("000001", "请求参数错误"),

    ;

    private final String code;
    private final String message;


    RespCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
