package com.hduong25.javalearn.utils.enums;

import lombok.AllArgsConstructor;

/**
 * @author: hduong25
 */

@AllArgsConstructor
public enum ErrorResponse implements BaseEnumResponseCode {
    SERVER_INVALID("ERROR_001", "Lỗi hệ thống, vui lòng thử lại sau"),
    NOT_FOUND("ERROR_002", "Tài nguyên không tìm thấy"),
    BAD_REQUEST("ERROR_003", "Yêu cầu không hợp lệ"),
    UNAUTHORIZED("ERROR_004", "Chưa được phép truy cập");

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
