package com.iclp.wechat.integration.wxgateway.controller;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;

@RestController
@Scope("request")
public class WxGatewayController {
    @RequestMapping(value = "/search", method = {RequestMethod.GET, RequestMethod.POST})
    public String search(@RequestParam(value = "name", required = false) String name,
                         @RequestParam(value = "signature", required = false) String signature,
                         @RequestParam(value = "timestamp", required = false) String timestamp,
                         @RequestParam(value = "nonce", required = false) String nonce,
                         @RequestParam(value = "openid", required = false) String openid,
                         @RequestParam(value = "echostr", required = false) String echostr,
                         HttpServletRequest request) {
        String object = restTemplate().getForObject("http://ppsearch:8888/txtsearch", String.class,name,signature,timestamp,nonce,openid,echostr);
        return object;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}