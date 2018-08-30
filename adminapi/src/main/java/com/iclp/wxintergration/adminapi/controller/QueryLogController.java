package com.iclp.wxintergration.adminapi.controller;

import com.google.gson.Gson;
import com.iclp.wxintergration.adminapi.springdata.repository.QueryLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Scope("request")
public class QueryLogController {

    @Autowired
    private QueryLogRepository queryLogRepository;

    @RequestMapping(value="/queryLog",method =  { RequestMethod.GET, RequestMethod.POST })
    public String queryLog() {
        Gson gson = new Gson();
        return gson.toJson(queryLogRepository.findAll());
    }
}
