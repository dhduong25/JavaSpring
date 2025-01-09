package com.hduong25.javalearn.utils.enums;

import lombok.Getter;

/**
 * @author: hduong25
 */

@Getter
public enum StatusEnum {
    ACTIVE("Hoạt động", "Trạng thái hoạt động"),
    INACTIVE("Không hoạt động", "Trạng thái không hoạt động");

    private final String name;
    private final String desc;

    StatusEnum(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }
}
