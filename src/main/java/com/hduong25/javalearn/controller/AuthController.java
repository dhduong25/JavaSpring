package com.hduong25.javalearn.controller;

import com.hduong25.javalearn.utils.result.Result;
import com.hduong25.javalearn.utils.result.ResultUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: Alphaway
 */

@RestController
@RequestMapping("auth/")
public class AuthController {

    @GetMapping("login")
    public Result<String> login() {
        return ResultUtils.success("Login successfully!");
    }

    @GetMapping("register")
    public Result<String> register() {
        return ResultUtils.success("Register successfully!");
    }
}
