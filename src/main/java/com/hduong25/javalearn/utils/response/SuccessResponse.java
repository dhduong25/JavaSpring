package com.hduong25.javalearn.utils.response;

import lombok.AllArgsConstructor;

/**
 * @author: hduong25
 */

@AllArgsConstructor
public enum SuccessResponse implements BaseEnumResponseCode {
    SUCCESS("SUCCESS_001", ""),
    PING_SUCCESS("SUCCESS_002", "Pong!!!");

    private final String code;
    private final String message;

    @Override
    public String code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }
}
