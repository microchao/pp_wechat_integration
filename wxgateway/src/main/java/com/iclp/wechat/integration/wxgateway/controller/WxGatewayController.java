package com.iclp.wechat.integration.wxgateway.controller;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@RestController
@Scope("request")
public class WxGatewayController {

    private Logger logger = LoggerFactory.getLogger(WxGatewayController.class);;

    @RequestMapping(value = "/search", method = {RequestMethod.GET, RequestMethod.POST})
    public String search(@RequestParam(value = "name", required = false) String name,
                         @RequestParam(value = "signature", required = false) String signature,
                         @RequestParam(value = "timestamp", required = false) String timestamp,
                         @RequestParam(value = "nonce", required = false) String nonce,
                         @RequestParam(value = "openid", required = false) String openid,
                         @RequestParam(value = "echostr", required = false) String echostr,
                         HttpServletRequest request) {
        if(echostr!= null ) {
            return echostr;
        }
        Map<String,String> map = new HashMap<>();
        map.put("name", name);
        map.put("signature",signature);
        map.put("timestamp",timestamp);
        map.put("nonce",nonce);
        map.put("openid",openid);
        map.put("echostr",echostr);
        Map requestMap = XmlUtil.parseXml(request);
        logger.info("此次请求内容：" + requestMap);
        if(requestMap.get("MsgType").equals("text")) {
            map.put("content",requestMap.get("Content").toString());
            map.put("toUserName",requestMap.get("ToUserName").toString());
            Gson gson = new Gson();
            String requestJson = gson.toJson(map);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> requestParam = new HttpEntity<String>(requestJson, headers);
            String object = restTemplate().postForObject("http://ppsearch:8888/txtsearch",requestParam,String.class);
            return object;
        }else{
            String xml = "<xml> " +
                    "       <ToUserName>"  + openid + "</ToUserName> " +
                    "       <FromUserName>" + requestMap.get("ToUserName") +"</FromUserName> " +
                    "       <CreateTime>12345678</CreateTime> " +
                    "       <MsgType>text</MsgType> " +
                    "       <Content>暂不支持该类型</Content> " +
                    "     </xml>";
            return xml;
        }

    }


    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        return restTemplate;
    }
}