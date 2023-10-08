package com.hevo.search.app.store;

import com.hevo.search.app.exception.ExternalStorageException;
import com.hevo.search.app.external.model.CloudStorageFile;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


@Component
@Slf4j
public class LocalFileStorage {

    @Value("${local.tempFolderPath}")
    private String localTempFolderPath;

    public void saveToLocal(ByteArrayOutputStream byteArrayOutputStream, CloudStorageFile cloudStorageFile) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(localTempFolderPath + "/" + cloudStorageFile.getTempNameForDownload());
            byteArrayOutputStream.writeTo(fos);
        } catch (IOException ioe) {
            log.error("Failed to download the content", ioe);
            throw new ExternalStorageException(ioe);
        } finally {
            IOUtils.closeQuietly(byteArrayOutputStream);
            IOUtils.closeQuietly(fos);
        }
    }

    public boolean delete(CloudStorageFile cloudStorageFile) {
        String filePath = localTempFolderPath + "/" + cloudStorageFile.getTempNameForDownload();
        File file = new File(filePath);
        return file.delete();

    }

    public String getPath(CloudStorageFile cloudStorageFile){
        return localTempFolderPath + "/" + cloudStorageFile.getTempNameForDownload();
    }
}
