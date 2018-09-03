package com.iclp.wxintergration.adminapi.controller;

import com.google.gson.Gson;
import com.iclp.wxintergration.adminapi.springdata.repository.QueryLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.*;

@RestController
@Scope("request")
public class QueryLogController {

    @Autowired
    private QueryLogRepository queryLogRepository;

    @CrossOrigin(origins = "http://localhost:8000")
    @GetMapping("/queryLog")
    public String queryLog() {
        Gson gson = new Gson();
        return gson.toJson(queryLogRepository.findAll());
    }
}
