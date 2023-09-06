package com.assignment.hevo.dtos;

import com.google.api.client.util.DateTime;

public class FileDto {
    private String id;
    private String name;
    private String path;
    private String content;
    private DateTime lastModified;

    public FileDto(String id, String name, String path, String content, DateTime lastModified){
        this.id = id;
        this.name = name;
        this.path = path;
        this.content = content;
        this.lastModified = lastModified;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public String getContent() {
        return content;
    }

    public DateTime getLastModified() {
        return lastModified;
    }
}
