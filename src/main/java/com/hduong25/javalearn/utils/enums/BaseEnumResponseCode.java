package com.hduong25.javalearn.utils.enums;

/**
 * @author: hduong25
 */

public interface BaseEnumResponseCode {
    String SUCCESS_CODE = "000";
    String SUCCESS_MESSAGE = "SUCCESS";
    String ERROR_CODE = "111";
    String ERROR_MESSAGE = "ERROR";

    String code();

    String message();
}
