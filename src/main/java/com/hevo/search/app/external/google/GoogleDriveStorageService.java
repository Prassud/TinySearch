package com.hevo.search.app.external.google;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.hevo.search.app.exception.ExternalStorageException;
import com.hevo.search.app.external.AbstractCloudStorageService;
import com.hevo.search.app.external.ICredentialProvider;
import com.hevo.search.app.external.model.CloudStorageFile;
import com.hevo.search.app.external.model.CloudStoreFileList;
import com.hevo.search.app.store.LocalFileStorage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.hevo.search.app.external.google.GoogleDriveServiceConstants.GOOGLE_DRIVE_STORAGE_TYPE;

@Component
@Slf4j
public class GoogleDriveStorageService extends AbstractCloudStorageService {

    public GoogleDriveStorageService(ICredentialProvider credentialProvider) {
        super(credentialProvider);
    }


    @Autowired
    private LocalFileStorage localFileStorage;


    @Autowired
    private GoogleSriveServiceFactory googleSriveServiceFactory;

    @Value("${google.drive.root_folder_id}")
    private String googleDriveRootFolderId;

    @Override
    public CloudStoreFileList listFiles() {
        try {
            Credential credentials = (Credential) getCredentials();
            Drive service = googleSriveServiceFactory.buildGoogleDriveClient(credentials);
            FileList fileList = service
                    .files()
                    .list()
                    .setPageSize(100)
                    .setQ("\'" + googleDriveRootFolderId + "\' in parents")
                    .setFields(" nextPageToken, files(id, name, fullFileExtension, parents)")
                    .execute();
            List<File> files = fileList.getFiles();
            if (files == null || files.isEmpty()) {
                log.info("No files found.");
                return null;
            } else {
                List<CloudStorageFile> cloudStorageFiles = files
                        .stream()
                        .map(file -> mapToCloudStorageFile(service, file))
                        .collect(Collectors.toList());
                return CloudStoreFileList
                        .builder()
                        .fileList(cloudStorageFiles)
                        .nextPageToken(fileList.getNextPageToken())
                        .build();
            }
        } catch (IOException e) {
            throw new ExternalStorageException(e);
        }
    }

    private CloudStorageFile mapToCloudStorageFile(Drive service, File file) {
        return CloudStorageFile
                .builder()
                .cloudStorageId(file.getId())
                .name(file.getName())
                .path(getPath(service, file))
                .extension(file.getFullFileExtension())
                .tempNameForDownload(file.getId() + "." + file.getFullFileExtension())
                .cloudStorageType(GOOGLE_DRIVE_STORAGE_TYPE)
                .build();
    }

    private String getPath(Drive service, File file) {
        StringBuffer paths = new StringBuffer("/");
        List<String> fileNameById = getFileNameById(service, file.getParents());
        Collections.reverse(fileNameById);
        fileNameById.forEach(eachString -> paths.append(eachString).append("/"));
        return paths.append(file.getName()).toString();
    }

    private List<String> getFileNameById(Drive service, List<String> ids) {
        return ids
                .stream()
                .map(id -> getFileByName(service, id))
                .filter(file -> !file.getExplicitlyTrashed())
                .map(File::getName)
                .collect(Collectors.toList());
    }

    private File getFileByName(Drive service, String id) {
        try {
            File execute = service
                    .files()
                    .get(id)
                    .setFields("id, name, explicitlyTrashed")
                    .execute();
            return execute;
        } catch (IOException e) {
            throw new ExternalStorageException(e);
        }
    }

    @Override
    public CloudStoreFileList download(CloudStoreFileList cloudStoreFileList) {
        cloudStoreFileList
                .getFileList()
                .stream()
                .parallel()
                .forEach(eachfile -> {
                    ByteArrayOutputStream byteArrayOutputStream = downloadContent(eachfile);
                    localFileStorage.saveToLocal(byteArrayOutputStream, eachfile);
                });
        return cloudStoreFileList;
    }

    @Override
    public boolean isExist(String cloudStorageId) {
        try {
            Credential credentials = (Credential) getCredentials();
            Drive service = googleSriveServiceFactory.buildGoogleDriveClient(credentials);
            List<String> fileNameById = getFileNameById(service, Collections.singletonList(cloudStorageId));
            return !fileNameById.isEmpty();
        } catch (ExternalStorageException exception) {
            log.error("Failed to find the file", exception);
            return false;
        }
    }

    private ByteArrayOutputStream downloadContent(CloudStorageFile cloudStorageFile) {
        Credential credentials = (Credential) getCredentials();
        Drive service = googleSriveServiceFactory.buildGoogleDriveClient(credentials);
        try {
            OutputStream outputStream = new ByteArrayOutputStream();
            service
                    .files()
                    .get(cloudStorageFile.getCloudStorageId())
                    .executeMediaAndDownloadTo(outputStream);

            return (ByteArrayOutputStream) outputStream;
        } catch (GoogleJsonResponseException e) {
            log.error("Unable to move file: " + e.getDetails());
            throw new ExternalStorageException(e);
        } catch (IOException e) {
            throw new ExternalStorageException(e);
        }
    }
}
