package com.hduong25.javalearn.service.impl;

import com.hduong25.javalearn.service.PingService;
import com.hduong25.javalearn.utils.enums.SuccessResponse;
import com.hduong25.javalearn.utils.response.ResponseUtils;
import com.hduong25.javalearn.utils.response.Result;
import org.springframework.stereotype.Service;

/**
 * @author: hduong25
 */

@Service
public class PingServiceImpl implements PingService {

    @Override
    public Result<String> pingApp() {
        return ResponseUtils.success(SuccessResponse.PING_SUCCESS);
    }

}
