package com.hduong25.javalearn.service.impl;

import com.hduong25.javalearn.service.PingService;
import com.hduong25.javalearn.utils.response.SuccessResponse;
import com.hduong25.javalearn.utils.result.ResultUtils;
import com.hduong25.javalearn.utils.result.Result;
import org.springframework.stereotype.Service;

/**
 * @author: hduong25
 */

@Service
public class PingServiceImpl implements PingService {

    @Override
    public Result<String> pingApp() {
        return ResultUtils.success(SuccessResponse.PING_SUCCESS);
    }

}
