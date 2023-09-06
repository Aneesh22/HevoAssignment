package com.assignment.hevo.entities;

import com.assignment.hevo.dtos.FileDto;
import com.google.api.client.util.DateTime;
import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "file_data")
public class FileData {
    @Id
    private String id;
    private String name;
    private String path;
    @Column(columnDefinition = "TEXT")
    private String fileContent;
    public String getFileName() {
        return name;
    }

    public String getFileUrl() {
        return path;
    }

    public void setFileContent(String fileContent) {
        this.fileContent = fileContent;
    }

    FileData(){
    }
    public FileData(String id, String name, String path, String fileContent, DateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.fileContent = fileContent;
    }
}
