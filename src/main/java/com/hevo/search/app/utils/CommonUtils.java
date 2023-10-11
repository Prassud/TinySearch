package com.hevo.search.app.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hevo.search.app.external.model.CloudStorageFile;

import java.util.Map;

public class CommonUtils {
    public static Map<String, Object> fileToMap(CloudStorageFile cloudStorageFile, ObjectMapper objectMapper) {
        return objectMapper.convertValue(cloudStorageFile,
                new TypeReference<Map<String, Object>>() {
                });
    }
}
