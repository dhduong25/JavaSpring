package com.hduong25.javalearn.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: hduong25
 */

@Validated
@RestController
@RequestMapping("/sendMail/")
public class SendMailController {
}
