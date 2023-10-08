package com.hevo.search.app.store;

import com.hevo.search.app.entity.Document;

import java.util.List;

public interface SearchAppStore {
    List<Document> search(String queryParam);
}
