package com.hduong25.javalearn.utils.result;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author: hduong25
 */

@Getter
@Setter
@AllArgsConstructor
public class ErrorDetails {
    protected String errorCode;

    protected String location;

    protected String message;
}
