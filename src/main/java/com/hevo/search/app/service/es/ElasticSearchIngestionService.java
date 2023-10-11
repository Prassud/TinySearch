package com.hevo.search.app.service.es;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hevo.search.app.entity.Document;
import com.hevo.search.app.external.ICloudStorageService;
import com.hevo.search.app.external.model.CloudStorageFile;
import com.hevo.search.app.external.model.CloudStoreFileList;
import com.hevo.search.app.service.AbstractIngestionService;
import com.hevo.search.app.store.SearchAppStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.hevo.search.app.utils.CommonUtils.fileToMap;
import static com.hevo.search.app.utils.Constants.ELASTIC_SEARCH;

@Slf4j
@Component
public class ElasticSearchIngestionService extends AbstractIngestionService {

    private final SearchAppStore searchAppStore;
    private ICloudStorageService iCloudStorageService;

    private final ObjectMapper objectMapper;

    @Autowired
    public ElasticSearchIngestionService(ICloudStorageService iCloudStorageService,
                                         SearchAppStore searchAppStore,
                                         ObjectMapper objectMapper) {
        super(iCloudStorageService);
        this.searchAppStore = searchAppStore;
        this.objectMapper = objectMapper;
    }

    @Override
    public void ingestionExtended(CloudStoreFileList cloudStoreFileList) {
        List<Document> documents = cloudStoreFileList.getFileList().stream().map(this::mapToDocument)
                .collect(Collectors.toList());
        searchAppStore.save(documents);
    }

    private Document mapToDocument(CloudStorageFile eachFile) {
        Document document = new Document();
        document.setId(getId(eachFile));
        document.setContent(eachFile.getContent());
        Map<String, Object> external = fileToMap(eachFile, objectMapper);
        external.remove("content");
        document.setExternal(external);
        return document;
    }

    private String getId(CloudStorageFile eachFile) {
        return UUID.nameUUIDFromBytes((eachFile
                        .getCloudStorageType() + "_" + eachFile.getCloudStorageId()).getBytes())
                .toString();
    }

    @Override
    public String getIngestionType() {
        return ELASTIC_SEARCH;
    }

    @Override
    protected boolean isSaveToLocal() {
        return false;
    }
}
