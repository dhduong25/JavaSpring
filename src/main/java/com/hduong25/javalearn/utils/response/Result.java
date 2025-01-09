package com.hduong25.javalearn.utils.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * @author: hduong25
 */

@Getter
@Setter
@Builder
public class Result<T> {
    protected Integer status;

    protected String code;

    protected String message;

    protected ErrorDetails error;

    protected T data;
}
