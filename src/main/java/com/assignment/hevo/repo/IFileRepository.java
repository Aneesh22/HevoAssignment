package com.assignment.hevo.repo;

import com.assignment.hevo.entities.FileData;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IFileRepository extends CrudRepository<FileData, String> {
    @Query(value = "SELECT * FROM file_data where file_content like %?1%", nativeQuery = true)
    public List<FileData> findByContext(String query);

    public List<FileData> findByName(String name);
}
