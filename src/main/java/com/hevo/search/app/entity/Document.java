package com.hevo.search.app.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Document {
    private String content;
    private String id;
    private String attachment;
    private File file;
    private Path path;
    private Map<String, Object> object;
    private Map<String, Object> external;
}
