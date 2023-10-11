package com.hevo.search.app.store;

import com.hevo.search.app.entity.Document;
import com.hevo.search.app.external.model.CloudStoreFileList;

import java.util.List;

public interface SearchAppStore {
    List<Document> search(String queryParam);
    void save(List<Document> documents);
}
