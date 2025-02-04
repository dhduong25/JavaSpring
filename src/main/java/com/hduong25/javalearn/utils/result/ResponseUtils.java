package com.hduong25.javalearn.utils.result;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author: hduong25
 */

@Getter
@Setter
@NoArgsConstructor
public class ResponseUtils<T> {
    private Integer status;
    private String code;
    private String message;
    private ErrorDetails error;
    private T data;

    private ResponseUtils(T data) {
        this.data = data;
    }

    public static <T> ResponseUtils<T> ok(T data) {
        return response(data);
    }

    public static <T> ResponseUtils<T> error(T data) {
        return response(data);
    }

    private static <T> ResponseUtils<T> response(T data) {
        return new ResponseUtils<>(data);
    }
}
