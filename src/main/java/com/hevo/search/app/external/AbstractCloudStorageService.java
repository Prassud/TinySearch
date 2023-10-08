package com.hevo.search.app.external;

import com.hevo.search.app.external.model.CloudStorageFile;

import java.util.List;

public abstract class AbstractCloudStorageService implements ICloudStorageService{
    private final ICredentialProvider credentialProvider;

    public AbstractCloudStorageService(ICredentialProvider credentialProvider) {
        this.credentialProvider = credentialProvider;
    }

    protected Object getCredentials() {
        return credentialProvider.getCredential();
    }
}
