package com.hduong25.javalearn.utils.response;

import com.hduong25.javalearn.utils.constants.AppConstants;
import com.hduong25.javalearn.utils.constants.HttpStatusConstants;
import com.hduong25.javalearn.utils.enums.BaseEnumResponseCode;
import com.hduong25.javalearn.utils.enums.ErrorResponse;

/**
 * @author: hduong25
 */

public class ResponseUtils {

    private ResponseUtils() {
    }

    // Success
    public static <T> Result<T> success(T data) {
        return Result.<T>builder()
                .status(HttpStatusConstants.OK)
                .code(BaseEnumResponseCode.SUCCESS_CODE)
                .message(BaseEnumResponseCode.SUCCESS_MESSAGE)
                .error(null)
                .data(data)
                .build();
    }

    public static <T> Result<T> success(Integer status, T data) {
        return Result.<T>builder()
                .status(status)
                .code(BaseEnumResponseCode.SUCCESS_CODE)
                .message(BaseEnumResponseCode.SUCCESS_MESSAGE)
                .error(null)
                .data(data)
                .build();

    }

    public static <T> Result<T> success(Integer status, String message, T data) {
        return Result.<T>builder()
                .status(status)
                .code(BaseEnumResponseCode.SUCCESS_CODE)
                .message(message)
                .error(null)
                .data(data)
                .build();
    }

    public static Result<String> success(Enum<?> enumResponse) {
        if (enumResponse instanceof BaseEnumResponseCode response) {
            return Result.<String>builder()
                    .status(HttpStatusConstants.OK)
                    .code(BaseEnumResponseCode.SUCCESS_CODE)
                    .message(BaseEnumResponseCode.SUCCESS_MESSAGE)
                    .error(null)
                    .data(response.message())
                    .build();
        }

        throw new IllegalArgumentException(BaseEnumResponseCode.ERROR_MESSAGE);
    }

    // Error
    public static <T> Result<T> error(String message) {
        return Result.<T>builder()
                .status(HttpStatusConstants.BAD_REQUEST)
                .code(BaseEnumResponseCode.ERROR_CODE)
                .message(BaseEnumResponseCode.ERROR_MESSAGE)
                .data(null)
                .error(new ErrorDetails(ErrorResponse.SERVER_INVALID.code(), AppConstants.NAME_APP, message))
                .build();
    }

    public static <T> Result<T> error(int status, String location, String message) {
        return Result.<T>builder()
                .status(status)
                .code(BaseEnumResponseCode.ERROR_CODE)
                .message(BaseEnumResponseCode.ERROR_MESSAGE)
                .data(null)
                .error(new ErrorDetails(ErrorResponse.SERVER_INVALID.code(), location, message))
                .build();
    }

    public static <T> Result<T> error(String location, Enum<?> enumResponse) {
        if (enumResponse instanceof BaseEnumResponseCode response) {
            return Result.<T>builder()
                    .status(HttpStatusConstants.BAD_REQUEST)
                    .code(BaseEnumResponseCode.ERROR_CODE)
                    .message(BaseEnumResponseCode.ERROR_MESSAGE)
                    .data(null)
                    .error(new ErrorDetails(response.code(), location, response.message()))
                    .build();
        }

        throw new IllegalArgumentException(BaseEnumResponseCode.ERROR_MESSAGE);
    }

    public static <T> Result<T> error(Enum<?> enumResponse) {
        if (enumResponse instanceof BaseEnumResponseCode response) {
            return Result.<T>builder()
                    .status(HttpStatusConstants.BAD_REQUEST)
                    .code(BaseEnumResponseCode.ERROR_CODE)
                    .message(BaseEnumResponseCode.ERROR_MESSAGE)
                    .data(null)
                    .error(new ErrorDetails(response.code(), AppConstants.NAME_APP, response.message()))
                    .build();
        }

        throw new IllegalArgumentException(BaseEnumResponseCode.ERROR_MESSAGE);
    }
}
