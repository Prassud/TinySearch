package com.hevo.search.app.external.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CloudStoreFileList {
    private List<CloudStorageFile> fileList;
    private String nextPageToken;
}
