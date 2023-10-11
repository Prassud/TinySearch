package com.hevo.search.app.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hevo.search.app.external.ICloudStorageService;
import com.hevo.search.app.external.ICredentialProvider;
import com.hevo.search.app.external.google.GoogleDriveStorageService;
import com.hevo.search.app.external.google.GoogleSriveServiceFactory;
import com.hevo.search.app.external.parser.ContentParser;
import com.hevo.search.app.service.IngestionService;
import com.hevo.search.app.store.LocalFileStorage;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
@ComponentScan(basePackages = { "com.hevo.search.app.*" })
public class SearchAppConfig{

    @Bean("iCloudStorageService")
    public ICloudStorageService iCloudStorageService(ICredentialProvider credentialProvider,
                                                     LocalFileStorage localFileStorage,
                                                     List<ContentParser> contentParsers,
                                                     GoogleSriveServiceFactory googleSriveServiceFactory) {
        return  new GoogleDriveStorageService(credentialProvider, localFileStorage, contentParsers, googleSriveServiceFactory);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return  new ObjectMapper();
    }

    @Bean("ingestionServiceMap")
    public Map<String, IngestionService> ingestionServiceMap(List<IngestionService> ingestionServices) {
        return  ingestionServices.stream().collect(Collectors.toMap(IngestionService::getIngestionType, Function.identity()));
    }

    @Bean
    public CloseableHttpClient httpclient() {
        return HttpClientBuilder.create().build();
    }
}