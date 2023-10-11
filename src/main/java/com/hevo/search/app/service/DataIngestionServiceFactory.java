package com.hevo.search.app.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class DataIngestionServiceFactory {

    @Value("${ingestion.type:ELASTICE_SEARCH}")
    private String ingestionType;
    @Autowired
    @Qualifier("ingestionServiceMap")
    private Map<String, IngestionService> ingestionServiceMap;

    public IngestionService getIngestionService() {
        return ingestionServiceMap.get(ingestionType);
    }
}
