package com.hevo.search.app.external;

import com.hevo.search.app.external.model.CloudStorageFile;
import com.hevo.search.app.external.parser.ContentParser;
import com.hevo.search.app.store.LocalFileStorage;

import java.io.ByteArrayOutputStream;
import java.util.List;

public abstract class AbstractCloudStorageService implements ICloudStorageService {
    private final ICredentialProvider credentialProvider;

    private final LocalFileStorage localFileStorage;

    private List<ContentParser> contentParsers;

    public AbstractCloudStorageService(ICredentialProvider credentialProvider, LocalFileStorage localFileStorage, List<ContentParser> contentParsers) {
        this.credentialProvider = credentialProvider;
        this.localFileStorage = localFileStorage;
        this.contentParsers = contentParsers;
    }

    protected Object getCredentials() {
        return credentialProvider.getCredential();
    }

    protected void save(CloudStorageFile eachfile,
                        ByteArrayOutputStream byteArrayOutputStream,
                        boolean saveToLocal) {
        if (saveToLocal) {
            localFileStorage.saveToLocal(byteArrayOutputStream, eachfile);
        } else {
            String extension = eachfile.getExtension();
            eachfile.setContent(getParser(extension).parse(byteArrayOutputStream));
        }
    }

    private ContentParser getParser(String fileExtension) {
        return contentParsers.stream()
                .filter(parser -> parser.getContentTypeSupported().contains(fileExtension))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Unable to find the pareser for file extension " + fileExtension));

    }
}
