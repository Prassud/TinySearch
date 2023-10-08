package com.hevo.search.app.service;

import com.hevo.search.app.external.ICloudStorageService;
import com.hevo.search.app.external.model.CloudStoreFileList;
import com.hevo.search.app.model.FileUploadResponse;
import com.hevo.search.app.store.FsCrawlerStore;
import com.hevo.search.app.store.LocalFileStorage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Component
@Slf4j
public class FileLoaderService {
    @Autowired
    private ICloudStorageService iCloudStorageService;

    @Autowired
    private LocalFileStorage localFileStorage;

    @Autowired
    private FsCrawlerStore fsCrawlerStore;

    public void load() {
        CloudStoreFileList actualList = iCloudStorageService.listFiles();
        try {
            CompletableFuture.supplyAsync(() -> actualList)
                    .thenApply((cloudStoreFileList -> {
                        log.info("Downloading the file");
                        return iCloudStorageService.download(cloudStoreFileList);
                    }))
                    .thenAccept(this::uploadFile)
                    .get();
        } catch (InterruptedException | ExecutionException e) {
            log.info("Failed to load the file", e);
            throw new RuntimeException(e);
        }
    }

    private void uploadFile(CloudStoreFileList cloudStoreFileList) {
        cloudStoreFileList.getFileList().stream()
                .parallel()
                .forEach(file -> {
                    log.info("Uploading the file {}", file.getName());
                    FileUploadResponse response = fsCrawlerStore.fileUpload(file);
                    log.info("Deleting the file in local storage {}", file.getTempNameForDownload());
                    localFileStorage.delete(file);
                });
    }
}
