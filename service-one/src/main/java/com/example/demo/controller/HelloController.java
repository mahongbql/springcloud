package com.example.demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    private Logger log = LoggerFactory.getLogger(HelloController.class);

    @Value("${config}")
    private String config;

    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    public String hello() {
        log.info("service-one accept ------------------------------ ");

        return this.config;
    }
}
