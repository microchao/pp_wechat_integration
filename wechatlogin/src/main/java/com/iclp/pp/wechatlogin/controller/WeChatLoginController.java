package com.iclp.pp.wechatlogin.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.iclp.wechat.util.WeChatLoginUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;


@RestController
public class WeChatLoginController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(value = "/",method = RequestMethod.GET)
    String home(@RequestParam(value="code") String code, @RequestParam(value="state", required=false) String state) {
        WeChatLoginUtil weChatLoginUtil = new WeChatLoginUtil(code);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(weChatLoginUtil.getUserInfo());
        String userInfo = gson.toJson(je);
        try {
            userInfo = new String(userInfo.getBytes("ISO-8 859-1"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return userInfo;
    }

}
