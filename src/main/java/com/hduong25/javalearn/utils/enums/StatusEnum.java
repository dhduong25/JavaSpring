package com.hduong25.javalearn.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author: hduong25
 */

@Getter
@AllArgsConstructor
public enum StatusEnum implements InterfaceEnum {
    ACTIVE("Hoạt động", "Trạng thái hoạt động"),
    INACTIVE("Không hoạt động", "Trạng thái không hoạt động");

    private final String code;
    private final String message;

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
