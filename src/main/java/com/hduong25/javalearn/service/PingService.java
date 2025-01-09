package com.hduong25.javalearn.service;

import com.hduong25.javalearn.utils.response.Result;

/**
 * @author: hduong25
 */

public interface PingService {
    Result<String> pingApp();
}
