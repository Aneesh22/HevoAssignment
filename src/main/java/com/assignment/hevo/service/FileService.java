package com.assignment.hevo.service;

import com.assignment.hevo.dtos.FileDto;
import com.assignment.hevo.entities.FileData;
import com.assignment.hevo.exceptions.NoFileFoundException;
import com.assignment.hevo.repo.IFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service("searchService")
public class FileService {

    @Autowired
    IFileRepository fileRepository;

    public List<String> searchFile(String query) throws Exception {
        var data = fileRepository.findByContext(query);
        var result = data.stream().map(FileData::getFileUrl).toList();
        if (!result.isEmpty()){
            return result;
        } else {
            throw new NoFileFoundException("No Results Found");
        }
    }

    public void upsertFileData(FileDto fileDto) throws Exception {
        // check if file exists
        var data = fileRepository.findByName(fileDto.getName());
        if (data.size()>0){
            // update file
            var file = data.get(0);
            file.setFileContent(fileDto.getContent());
            fileRepository.save(file);
        } else {
            // create file
            var fileData = createDtoToDal(fileDto);
            fileRepository.save(fileData);
        }
    }

    public void deleteFile(String id) throws Exception {
        fileRepository.deleteById(id);
    }

    FileData createDtoToDal(FileDto fileDto){
        var fileData = new FileData(fileDto.getId(), fileDto.getName(), fileDto.getPath(), fileDto.getContent(),
                fileDto.getLastModified());
        return fileData;
    }
}

