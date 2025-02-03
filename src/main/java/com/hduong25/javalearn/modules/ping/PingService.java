package com.hduong25.javalearn.modules.ping;

import com.hduong25.javalearn.utils.result.Result;

/**
 * @author: hduong25
 */

public interface PingService {
    Result<String> pingApp();
}
