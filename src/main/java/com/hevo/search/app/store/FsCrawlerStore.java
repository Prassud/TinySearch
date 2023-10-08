package com.hevo.search.app.store;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hevo.search.app.external.model.CloudStorageFile;
import com.hevo.search.app.model.FileUploadResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Repository
@Slf4j
public class FsCrawlerStore {

    public static final String FILE = "file";

    private final CloseableHttpClient httpclient = HttpClientBuilder.create().build();

    @Value("${fscrawler.url}")
    private String fsCrawlerUrl;

    @Autowired
    private LocalFileStorage localFileStorage;

    @Autowired
    private ObjectMapper objectMapper;

    public FileUploadResponse fileUpload(CloudStorageFile cloudStorageFile) {
        try {
            UUID boundary = UUID.randomUUID();
            HttpEntity entity = buildHttpEntity(cloudStorageFile, boundary);
            HttpUriRequest multipartRequest = buildRequestBuilder(fsCrawlerUrl, boundary, entity);
            log.debug("Request sent to fs crawler service -- " + multipartRequest);
            String responseContent = execute(httpclient, multipartRequest);
            return objectMapper.readValue(responseContent, FileUploadResponse.class);
        } catch (IOException e) {
            log.error("Failed to upload file to fs crawler service", e);
        }
        return null;
    }

    private static HttpUriRequest buildRequestBuilder(String url, UUID boundary, HttpEntity entity) {
        RequestBuilder requestBuilder = RequestBuilder.post(url + "?debug=true");
        requestBuilder.addHeader("Accept", "*/*");
        requestBuilder.addHeader("content-type", "multipart/form-data; boundary=--" + boundary);
        requestBuilder.setEntity(entity);
        return requestBuilder.build();
    }

    private HttpEntity buildHttpEntity(CloudStorageFile cloudStorageFile, UUID boundary) throws JsonProcessingException {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.RFC6532);
        Map<String, Object> map = fileToMap(cloudStorageFile);
        Map<String, Object> external = new HashMap<>();
        external.put("external", map);
        builder.addBinaryBody(FILE, new File(localFileStorage.getPath(cloudStorageFile)), ContentType.DEFAULT_BINARY, cloudStorageFile.getPath());
        builder.setBoundary("--" + boundary);
        builder.addBinaryBody("tags", new ByteArrayInputStream(objectMapper.writeValueAsBytes(external)));
        builder.setBoundary("--" + boundary);
        return builder.build();
    }

    private Map<String, Object> fileToMap(CloudStorageFile cloudStorageFile) {
        return objectMapper.convertValue(cloudStorageFile,
                new TypeReference<Map<String, Object>>() {
                });
    }

    private String execute(CloseableHttpClient httpclient, HttpUriRequest post) throws IOException {
        HttpResponse response = httpclient.execute(post);
        if (response != null) {
            log.debug("Response received from fs crawler service {}", response);
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {
                BufferedReader br = null;
                InputStream responseStream = responseEntity.getContent();
                try {
                    if (responseStream != null) {
                        br = new BufferedReader(new InputStreamReader(responseStream));
                        return getResponseContent(br);
                    }
                } finally {
                    IOUtils.closeQuietly(br);
                    IOUtils.closeQuietly(responseStream);
                }
            }
        }
        return null;
    }

    private String getResponseContent(BufferedReader br) throws IOException {
        String responseLine = br.readLine();
        String tempResponseString = "";
        while (responseLine != null) {
            tempResponseString = tempResponseString + responseLine + System.getProperty("line.separator");
            responseLine = br.readLine();
        }
        return tempResponseString;
    }
}
