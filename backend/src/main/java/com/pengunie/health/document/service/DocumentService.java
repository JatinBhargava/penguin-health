package com.pengunie.health.document.service;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.pengunie.health.document.entity.Document;
import com.pengunie.health.document.repository.DocumentRepository;

@Service
public class DocumentService {

    private static final String DEMO_USER_ID = "demo-user";

    private final DocumentRepository documentRepository;
    private final DocumentStorageService storageService;
    private final PdfTextExtractionService pdfTextExtractionService;
    private final DocumentChunkingService chunkingService;

    public DocumentService(
            DocumentRepository documentRepository,
            DocumentStorageService storageService,
            PdfTextExtractionService pdfTextExtractionService,
            DocumentChunkingService chunkingService) {

        this.documentRepository = documentRepository;
        this.storageService = storageService;
        this.pdfTextExtractionService = pdfTextExtractionService;
        this.chunkingService = chunkingService;
    }

    @Transactional
    public Document upload(MultipartFile file) throws Exception {

        validatePdf(file);

        Document document = new Document();

        document.setUserId(DEMO_USER_ID);
        document.setFileName(file.getOriginalFilename());
        document.setContentType(file.getContentType());
        document.setFileSize(file.getSize());
        document.setStatus("PROCESSING");

        String storagePath = storageService.store(file);

        document.setStoragePath(storagePath);

        document = documentRepository.save(document);

        try {
            Path pdfPath = Paths.get(storagePath);

            String extractedText
                    = pdfTextExtractionService.extractText(pdfPath);

            document.setExtractedText(extractedText);

            document = documentRepository.save(document);

            chunkingService.createChunks(
                    document.getId(),
                    extractedText
            );
            document.setStatus("PROCESSED");

        } catch (Exception e) {

            document.setStatus("FAILED");

            throw e;
        }

        return documentRepository.save(document);
    }

    private void validatePdf(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }

        if (!"application/pdf".equalsIgnoreCase(file.getContentType())) {
            throw new IllegalArgumentException("Only PDF files are supported");
        }
    }
}
