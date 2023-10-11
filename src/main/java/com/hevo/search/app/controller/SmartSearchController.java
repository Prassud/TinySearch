package com.hevo.search.app.controller;


import com.hevo.search.app.entity.Document;
import com.hevo.search.app.service.DataIngestionServiceFactory;
import com.hevo.search.app.service.fscrawler.FsCrawlerIngestionService;
import com.hevo.search.app.service.SmartSearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.List;

@RestController
@RequestMapping("/api")
@Slf4j
public class SmartSearchController {

    @Autowired
    private FsCrawlerIngestionService fsCrawlerIngestionService;

    @Autowired
    private DataIngestionServiceFactory dataIngestionServiceFactory;

    @Autowired
    private SmartSearchService smartSearchService;

    @GetMapping("/search")
    public String search(@RequestParam("query") String queryParam) {
        List<Document> docment = smartSearchService.search(queryParam);
        StringBuilder builder = new StringBuilder();
        docment.forEach(document -> {
            Object cloudStorageType = document.getExternal().get("cloudStorageType");
            Object path = document.getExternal().get("path");
            builder.append(cloudStorageType).append("=====").append(path).append(System.lineSeparator());
        });
        if (builder.length() > 0) {
            builder.deleteCharAt(builder.length() - 1);
        }
        return builder.toString();
    }

    @PostMapping("/refresh")
    public ResponseEntity load() {
        dataIngestionServiceFactory.getIngestionService().refresh();
        return ResponseEntity.ok().build();
    }

    @PostConstruct
    public void init() {
        log.info("Loading the data from Cloud storage");
        dataIngestionServiceFactory.getIngestionService().refresh();
    }
}
