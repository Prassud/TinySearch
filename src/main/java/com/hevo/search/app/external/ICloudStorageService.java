package com.hevo.search.app.external;

import com.hevo.search.app.external.model.CloudStoreFileList;

public interface ICloudStorageService {
     CloudStoreFileList listFiles();
     CloudStoreFileList download(CloudStoreFileList cloudStoreFileList, boolean saveToLocal);
     boolean isExist(String cloudStorageId);
}
