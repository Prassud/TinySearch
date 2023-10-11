package com.hevo.search.app.service;

import com.hevo.search.app.external.ICloudStorageService;
import com.hevo.search.app.external.model.CloudStoreFileList;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
public abstract class AbstractIngestionService implements IngestionService {

    private final ICloudStorageService iCloudStorageService;
    public AbstractIngestionService(ICloudStorageService cloudStorageService) {
        this.iCloudStorageService = cloudStorageService;
    }

    public void ingest() {
        CloudStoreFileList cloudStoreFileList = iCloudStorageService.listFiles();
        try {
            CompletableFuture
                    .supplyAsync(() -> cloudStoreFileList)
                    .thenApply((files -> {
                        log.info("Downloading the file content");
                        return iCloudStorageService.download(files, isSaveToLocal());
                    }))
                    .thenAccept(this::ingestionExtended)
                    .get();
        } catch (InterruptedException | ExecutionException e) {
            log.info("Failed to ingest the file", e);
            throw new RuntimeException(e);
        }
    }

    protected boolean isSaveToLocal() {
        return true;
    }

    @Override
    public void refresh() {
        ingest();
    }

    public abstract void ingestionExtended(CloudStoreFileList cloudStoreFileList);
}
