package com.hevo.search.app.external.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CloudStorageFile {
    private String name;
    private String cloudStorageId;
    private String path;
    private String extension;
    private String tempNameForDownload;
    private String cloudStorageType;

}
