package com.hduong25.javalearn.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RoleEnums implements InterfaceEnum {
    ADMIN("ADMIN", "Quan tri vien"),
    EMPLOYEE("EMPLOYEE", "Nhan vien");

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