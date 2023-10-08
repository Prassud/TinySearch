package com.hevo.search.app.external.google;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.drive.Drive;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;

import static com.hevo.search.app.external.google.GoogleDriveServiceConstants.APPLICATION_NAME;
import static com.hevo.search.app.external.google.GoogleDriveServiceConstants.JSON_FACTORY;

@Component
public class GoogleSriveServiceFactory {
    private NetHttpTransport netHttpTransport;

    public Drive buildGoogleDriveClient(Credential credentials) {
        return new Drive
                .Builder(getNetHttpTransport(), JSON_FACTORY, credentials)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public NetHttpTransport getNetHttpTransport() {
        if (null == netHttpTransport) {
            try {
                netHttpTransport = GoogleNetHttpTransport.newTrustedTransport();
            } catch (GeneralSecurityException | IOException e) {
                throw new RuntimeException(e);
            }
        }
        return netHttpTransport;
    }
}
