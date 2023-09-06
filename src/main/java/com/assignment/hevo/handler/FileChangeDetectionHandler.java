package com.assignment.hevo.handler;

import com.assignment.hevo.exceptions.ChangeDetectorException;
import com.assignment.hevo.utils.GoogleDriveUtils;
import com.assignment.hevo.dtos.FileDto;
import com.assignment.hevo.service.FileService;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.Change;
import com.google.api.services.drive.model.ChangeList;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class FileChangeDetectionHandler implements Runnable {
    String pageToken;
    FileService fileService;
    Drive driveClient;

    public FileChangeDetectionHandler(FileService fileService) throws IOException {
        this.pageToken = new GoogleDriveUtils().fetchStartPageToken();
        this.driveClient = getDriverClient();
        this.fileService = fileService;
    }

    @Override
    public void run() {
        do{
            try {
                System.out.println("Checking if there is any change in the drive...");
                pageToken = fetchChanges(pageToken);
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }while (true);
    }

    Drive getDriverClient() throws IOException {
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
        return service;
    }

    public String fetchChanges(String savedStartPageToken) throws ChangeDetectorException {

        try {
            String pageToken = savedStartPageToken;
            while (pageToken != null) {
                ChangeList changes = this.driveClient.changes().list(pageToken)
                        .execute();
                for (com.google.api.services.drive.model.Change change : changes.getChanges()) {
                    handleFileChange(change);
                }
                if (changes.getNewStartPageToken() != null) {
                    // Last page, save this token for the next polling interval
                    savedStartPageToken = changes.getNewStartPageToken();
                }
                pageToken = changes.getNextPageToken();

            }

            return savedStartPageToken;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            throw new ChangeDetectorException(e.getMessage());
        }
    }

    // TODO This can be improved by using adding these events to a queue.
    private void handleFileChange(Change change) throws Exception {
        // Handle deletes
        if (change.getRemoved()){
            fileService.deleteFile(change.getFileId());
            return;
        }

        // Handle update and inserts
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        this.driveClient.files().get(change.getFileId())
                .executeMediaAndDownloadTo(outputStream);
        System.out.println("Change found for file: " + change.getFileId() + "Inserting/Updating the file");
        FileDto fileDto =
                new FileDto(change.getFileId(), change.getFile().getName(), change.getFile().getName(),
                        outputStream.toString(), change.getTime());
        fileService.upsertFileData(fileDto);
    }
}
