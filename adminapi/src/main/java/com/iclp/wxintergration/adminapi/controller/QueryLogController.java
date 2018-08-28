package com.iclp.wxintergration.adminapi.controller;

import com.iclp.wxintergration.adminapi.springdata.repository.QueryLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@Scope("request")
public class QueryLogController {

    @Autowired
    private QueryLogRepository queryLogRepository;

    @RequestMapping(value="/queryLog",method =  { RequestMethod.GET, RequestMethod.POST })
    public String queryLog(@RequestBody String jsonString,
                           HttpServletRequest request) {
        queryLogRepository.findAll();
        return null;
    }
}
