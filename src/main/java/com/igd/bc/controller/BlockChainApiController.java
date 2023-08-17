package com.igd.bc.controller;

import com.igd.bc.model.APIGQLResponse;
import com.igd.bc.service.BlockChainApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/")
public class BlockChainApiController {

    @Autowired
    private BlockChainApiService service;

    @GetMapping("getGraphiQLData")
    public ResponseEntity<APIGQLResponse> getGraphiQLData(@RequestParam(required = false) String from,
                                                          @RequestParam(required = false) String to,
                                                          @RequestParam(required = false) String dateFrom,
                                                          @RequestParam(required = false) String orderBy){
        return service.getGraphiQLData(from, to, dateFrom, orderBy);
    }
}
