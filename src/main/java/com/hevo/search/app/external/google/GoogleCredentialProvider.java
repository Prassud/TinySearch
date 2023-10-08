package com.hevo.search.app.external.google;

import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.hevo.search.app.exception.CredentialException;
import com.hevo.search.app.exception.ExternalStorageException;
import com.hevo.search.app.external.ICredentialProvider;
import org.elasticsearch.common.inject.Inject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;

import static com.hevo.search.app.external.google.GoogleDriveServiceConstants.JSON_FACTORY;
import static com.hevo.search.app.external.google.GoogleDriveServiceConstants.SCOPES;
import static com.hevo.search.app.external.google.GoogleDriveServiceConstants.TOKENS_DIRECTORY_PATH;

@Component
public class GoogleCredentialProvider implements ICredentialProvider {

    @Value("${google.drive.credentials}")
    private String credentialPath;

    @Value("${google.drive.authcode.receiverPort}")
    private int authCodeRecieverPort;


    @Autowired
    private GoogleSriveServiceFactory googleSriveServiceFactory;

    @Override
    public Object getCredential() {
        InputStream in = this.getClass().getClassLoader().getResourceAsStream(credentialPath);
        if (in == null) {
            throw new ExternalStorageException("Resource not found: " + credentialPath);
        }
        GoogleClientSecrets clientSecrets;
        try {
            clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
            NetHttpTransport netHttpTransport = googleSriveServiceFactory.getNetHttpTransport();
            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow
                    .Builder(netHttpTransport, JSON_FACTORY, clientSecrets, SCOPES)
                    .setDataStoreFactory(new FileDataStoreFactory(getTokensDirectory()))
                    .setAccessType("offline")
                    .build();
            LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
            return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
        } catch (IOException e) {
            throw new CredentialException("Failed to get google credential" + e.getMessage());
        }
    }

    private static File getTokensDirectory() {
        return new File(TOKENS_DIRECTORY_PATH);
    }
}
