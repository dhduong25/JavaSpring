package com.hduong25.javalearn.utils.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author: hduong25
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HttpStatusConstants {
    // Success
    public static final int OK = 200;
    public static final int CREATED = 201;

    // Errors
    public static final int BAD_REQUEST = 400;
    public static final int ERROR = 500;
}
