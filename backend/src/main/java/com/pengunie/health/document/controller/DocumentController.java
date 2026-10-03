package com.pengunie.health.document.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.pengunie.health.document.entity.Document;
import com.pengunie.health.document.service.DocumentService;

@RestController
@RequestMapping("/api/v1/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Document> upload(
            @RequestParam("file") MultipartFile file) throws Exception {

        return ResponseEntity.ok(
                documentService.upload(file)
        );
    }
}