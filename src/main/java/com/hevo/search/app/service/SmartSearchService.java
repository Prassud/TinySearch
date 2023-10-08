package com.hevo.search.app.service;

import com.hevo.search.app.entity.Document;
import com.hevo.search.app.store.SearchAppStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class SmartSearchService {

    @Autowired
    private SearchAppStore searchAppStore;

    public List<Document> search(String queryParam) {
        return searchAppStore.search(queryParam);
    }
}
