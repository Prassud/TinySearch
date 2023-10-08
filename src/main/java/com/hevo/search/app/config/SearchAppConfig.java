package com.hevo.search.app.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hevo.search.app.external.ICloudStorageService;
import com.hevo.search.app.external.ICredentialProvider;
import com.hevo.search.app.external.google.GoogleDriveStorageService;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@ComponentScan(basePackages = { "com.hevo.search.app.*" })
public class SearchAppConfig{

    @Bean("iCloudStorageService")
    public ICloudStorageService iCloudStorageService(ICredentialProvider credentialProvider) {
        return  new GoogleDriveStorageService(credentialProvider);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return  new ObjectMapper();
    }

    @Bean
    public CloseableHttpClient httpclient() {
        return HttpClientBuilder.create().build();
    }
}