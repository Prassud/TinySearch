package com.hevo.search.app.service.fscrawler;

import com.hevo.search.app.external.ICloudStorageService;
import com.hevo.search.app.external.model.CloudStoreFileList;
import com.hevo.search.app.service.AbstractIngestionService;
import com.hevo.search.app.store.FsCrawlerStore;
import com.hevo.search.app.store.LocalFileStorage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.hevo.search.app.utils.Constants.FS_CRAWLWER;

@Component
@Slf4j
public class FsCrawlerIngestionService extends AbstractIngestionService {

    private final ICloudStorageService iCloudStorageService;

    private final LocalFileStorage localFileStorage;

    private final FsCrawlerStore fsCrawlerStore;

    @Autowired
    public FsCrawlerIngestionService(ICloudStorageService iCloudStorageService, ICloudStorageService iCloudStorageService1, LocalFileStorage localFileStorage, FsCrawlerStore fsCrawlerStore) {
        super(iCloudStorageService);
        this.iCloudStorageService = iCloudStorageService1;
        this.localFileStorage = localFileStorage;
        this.fsCrawlerStore = fsCrawlerStore;
    }


    @Override
    public String getIngestionType() {
        return FS_CRAWLWER;
    }

    @Override
    public void ingestionExtended(CloudStoreFileList cloudStoreFileList) {
        cloudStoreFileList.getFileList().stream()
                .parallel()
                .forEach(file -> {
                    log.info("Uploading the file {}", file.getName());
                    fsCrawlerStore.fileUpload(file);
                    log.info("Deleting the file in local storage {}", file.getTempNameForDownload());
                    localFileStorage.delete(file);
                });
    }
}
