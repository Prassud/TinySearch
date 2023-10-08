package com.hevo.search.app.store;

import com.hevo.search.app.entity.Document;
import com.hevo.search.app.external.ICloudStorageService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.RegexpFlag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static org.elasticsearch.index.query.QueryBuilders.regexpQuery;

@Component
@Slf4j
public class ElasticSearchAppStore implements SearchAppStore {

    private static final String FS_SEARCH_INDEX = "job_name";
    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    @Autowired
    private ICloudStorageService iCloudStorageService;


    public List<Document> search(String queryParam) {
        Query searchQuery = new NativeSearchQueryBuilder()
                .withQuery(regexpQuery("content", queryParam + ".*")
                        .caseInsensitive(true)
                        .flags(RegexpFlag.ALL)).build();

        SearchHits<Document> search = elasticsearchOperations.search(searchQuery, Document.class, IndexCoordinates.of(FS_SEARCH_INDEX));
        List<SearchHit<Document>> searchHits = search.getSearchHits();
        Map<String, Document> documents = searchHits.stream().collect(Collectors.toMap(SearchHit::getId, SearchHit::getContent));
        List<Document> documentsToBeDeleted = new ArrayList<>();
        List<Document> result = new ArrayList<>();
        extractDocumentsToBeDeleted(documents, documentsToBeDeleted, result);
        delete(documentsToBeDeleted);
        return result;
    }

    private void extractDocumentsToBeDeleted(Map<String, Document> documents, List<Document> documentsToBeDeleted, List<Document> result) {
        for (Map.Entry<String, Document> documentSet : documents.entrySet()) {
            Document document = documentSet.getValue();
            document.setId(documentSet.getKey());
            if (Objects.nonNull(document.getExternal()) && document.getExternal().containsKey("cloudStorageId")) {
                String cloudStorageId = document.getExternal().get("cloudStorageId").toString();
                if (!iCloudStorageService.isExist(cloudStorageId)) {
                    documentsToBeDeleted.add(document);
                } else {
                    result.add(document);
                }
            }
        }
    }

    private void delete(List<Document> documentsToBeDeleted) {
        CompletableFuture.supplyAsync(() -> documentsToBeDeleted)
                .thenAccept((d) -> {
                    documentsToBeDeleted
                            .stream()
                            .parallel()
                            .forEach(eachDoc -> {
                                log.info("Delete the document {}", eachDoc.getId());
                                elasticsearchOperations.delete(eachDoc.getId());
                            });
                });
    }
}
