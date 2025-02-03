package com.hduong25.javalearn.modules.ping;

import com.hduong25.javalearn.utils.response.SuccessResponse;
import com.hduong25.javalearn.utils.result.Result;
import com.hduong25.javalearn.utils.result.ResultUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: hduong25
 */

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/ping")
public class PingController {

    @PostMapping
    public Result<String> ping() {
        return ResultUtils.success(SuccessResponse.PING_SUCCESS);
    }
}
