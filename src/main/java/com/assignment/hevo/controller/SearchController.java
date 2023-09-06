package com.assignment.hevo.controller;

import com.assignment.hevo.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/search")
public class SearchController {

@Autowired
FileService fileService;

    @GetMapping()
    public ResponseEntity<List<String>> findAllUsers(@RequestParam(required = false) String q) {
        List<String> data = null;
        try {
            data = fileService.searchFile(q);
            return new ResponseEntity<>(data, HttpStatusCode.valueOf(200));
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatusCode.valueOf(404));
        }
    }
}
