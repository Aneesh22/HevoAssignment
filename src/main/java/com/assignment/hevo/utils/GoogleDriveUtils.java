package com.assignment.hevo.utils;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.StartPageToken;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GoogleDriveUtils {

    Drive driverClient;

    public Drive getDriverClient() {
        return driverClient;
    }

    public GoogleDriveUtils() throws IOException {
        GoogleCredentials credentials = GoogleCredentials.getApplicationDefault()
                .createScoped(Arrays.asList(DriveScopes.DRIVE_FILE));
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(
                credentials);

        // Build a new authorized API client service.
        Drive service = new Drive.Builder(new NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                requestInitializer)
                .setApplicationName("Drive samples")
                .build();
        this.driverClient = service;
    }


    /**
     * Upload new file.
     *
     * @return Inserted file metadata if successful, {@code null} otherwise.
     * @throws IOException if service account credentials file not found.
     */

    public static void main(String[] args) throws IOException {
        new GoogleDriveUtils().uploadBasic();
        //new GoogleDriveUtils().deleteFile();
    }

    public String uploadBasic() throws IOException {

        // Upload file photo.jpg on drive.
        File fileMetadata = new File();
        fileMetadata.setName("folder1/test1.csv");
        // File's content.
        java.io.File filePath = new java.io.File("src/main/resources/test.csv");
        // Specify media type and file-path for file.
        FileContent mediaContent = new FileContent("file/csv", filePath);
        try {
            File file = this.driverClient.files().create(fileMetadata, mediaContent)
                    .setFields("id").execute();
            System.out.println("File ID: " + file.getId());
            return file.getId();
        } catch (GoogleJsonResponseException e) {
            // TODO(developer) - handle error appropriately
            System.err.println("Unable to upload file: " + e.getDetails());
            throw e;
        }
    }

    public void deleteFile() throws IOException {
        try {
            List<File> files = searchFile();
            for (File file : files) {
                this.driverClient.files().delete(file.getId()).execute();
                System.out.println("Deleted File ID: " + file.getId());
            }
        } catch (GoogleJsonResponseException e) {
            System.err.println("Unable to upload file: " + e.getDetails());
            throw e;
        }
    }

    public List<File> searchFile() throws IOException {
        Drive service = this.driverClient;
        List<File> files = new ArrayList<File>();
        String pageToken = null;
        do {
            FileList result = service.files().list()
                    .setQ("mimeType='file/csv'")
                    .setSpaces("drive")
                    .execute();
            for (File file : result.getFiles()) {
                System.out.printf("Found file: %s (%s)\n",
                        file.getName(), file.getId());
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

                service.files().get(file.getId())
                        .executeMediaAndDownloadTo(outputStream);
                OutputStream outputStream2 = new FileOutputStream("src/main/resources/test2.csv");
                outputStream.writeTo(outputStream2);
            }

            files.addAll(result.getFiles());
            pageToken = result.getNextPageToken();
        } while (pageToken != null);

        return files;
    }

    public String fetchStartPageToken() throws IOException {

        Drive service = this.driverClient;
        try {
            StartPageToken response = service.changes()
                    .getStartPageToken().execute();
            System.out.println("Start token: " + response.getStartPageToken());

            return response.getStartPageToken();
        } catch (GoogleJsonResponseException e) {
            System.err.println("Unable to fetch start page token: " + e.getDetails());
            throw e;
        }
    }

}