package com.hevo.search.app.controller;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.hevo.search.app.entity.Document;
import com.hevo.search.app.external.ICredentialProvider;
import com.hevo.search.app.external.google.GoogleDriveStorageService;
import com.hevo.search.app.external.google.GoogleSriveServiceFactory;
import com.hevo.search.app.external.model.CloudStorageFile;
import com.hevo.search.app.service.FileLoaderService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;

import static org.elasticsearch.index.query.QueryBuilders.idsQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Slf4j
@ComponentScan(basePackages = {"com.hevo.search.app.*"})
@TestMethodOrder(MethodOrderer.MethodName.class)
public class SmartSearchControllerTest {


    @Value("${google.drive.root_folder_id}")
    private String googleDriveRootFolderId;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    @Autowired
    private FileLoaderService fileLoaderService;


    @Autowired
    private GoogleSriveServiceFactory googleSriveServiceFactory;


    @Autowired
    private GoogleDriveStorageService googleDriveStorageService;

    @Autowired
    private ICredentialProvider iCredentialProvider;


    @BeforeEach
    public void beforeEach() {
        IndexCoordinates coordinates = IndexCoordinates.of("job_name");

        Query query = new NativeSearchQueryBuilder().withQuery(matchAllQuery()).build();
        String[] ids = getAllDocuments(coordinates, query);

        Query temp = new NativeSearchQueryBuilder().withQuery(idsQuery().addIds(ids)).build();
        elasticsearchOperations.delete(temp, Document.class, coordinates);
        fileLoaderService.load();
    }

    private String[] getAllDocuments(IndexCoordinates coordinates, Query query) {
        return elasticsearchOperations.search(query, Document.class, coordinates)
                .stream()
                .map(SearchHit::getId)
                .toArray(String[]::new);

    }

    @Test
    // Basic Search Content
    public void testcase_01() throws Exception {

        mockMvc.perform(get("/api/search?query=@gmail.com"))
                .andExpect(status().isOk())
                .andExpect(content().string("GOOGLE_DRIVE=====/temp-hevo/01.pdf"));
    }

    @Test
    // Ignore deleted file from storage
    public void testcase_02() throws Exception {
        mockMvc.perform(get("/api/search?query=DUMNNY1"))
                .andExpect(status().isOk())
                .andExpect(content().string("GOOGLE_DRIVE=====/temp-hevo/test4.csv"));

        Object credential = iCredentialProvider.getCredential();
        Drive drive = googleSriveServiceFactory.buildGoogleDriveClient((Credential) credential);
        CloudStorageFile cloudStorageFile = googleDriveStorageService
                .listFiles()
                .getFileList()
                .stream()
                .filter(f -> "test4.csv".equals(f.getName()))
                .findFirst()
                .get();
        deleteFile(drive, cloudStorageFile.getCloudStorageId());
        mockMvc.perform(get("/api/search?query=DUMNNY1"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
        upload(drive);
    }

    private static void deleteFile(Drive drive, String fileId) throws IOException {
        drive.files()
                .delete(fileId)
                .execute();
    }

    private void upload(Drive drive) throws IOException, URISyntaxException {
        File body = new File();
        body.setName("test4.csv");
        body.setParents(Collections.singletonList(googleDriveRootFolderId));
        java.io.File fileContent = new java.io.File(this.getClass().getClassLoader().getResource("data/test4.csv").toURI());
        FileContent mediaContent = new FileContent("text/plain", fileContent);
        try {
            File file = drive.files().create(body, mediaContent).execute();
            log.info(file.toString());
        } catch (Exception exception) {
            log.error("Failed to upload", exception);
        }
    }
}