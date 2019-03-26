package com.example.demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RefreshScope
@RestController
public class HelloController {

    private Logger log = LoggerFactory.getLogger(HelloController.class);

    @Value("${config}")
    private String config;

    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    public String hello() {
        log.info("service-one accept config :" + this.config);

        return this.config;
    }
}
