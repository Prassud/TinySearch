package com.hevo.search.app.external.google;

import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.DriveScopes;

import java.util.Arrays;
import java.util.List;

public class GoogleDriveServiceConstants {
    public static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    public static final String APPLICATION_NAME = "Google Drive API Java Quickstart";

    public static final String TOKENS_DIRECTORY_PATH = "tokens";

    public static final List<String> SCOPES = Arrays.asList(DriveScopes.DRIVE_METADATA_READONLY,
            DriveScopes.DRIVE_READONLY,
            DriveScopes.DRIVE_FILE, DriveScopes.DRIVE);
    public static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    public static final String GOOGLE_DRIVE_STORAGE_TYPE = "GOOGLE_DRIVE";

}
