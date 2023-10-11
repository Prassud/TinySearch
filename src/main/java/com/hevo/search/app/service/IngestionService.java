package com.hevo.search.app.service;

public interface IngestionService {
    void ingest();

    void refresh();

    String getIngestionType();
}
