package com.se.idoctor.web;

import com.se.idoctor.entity.Document;
import com.se.idoctor.service.DocumentService;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@RestController
@RequestMapping("/api/document")
public class DocumentController {
    private DocumentService documentService;

    @GetMapping("/{id}")
    public ResponseEntity<Document> getDocumentById(@PathVariable Long id) {
        return new ResponseEntity<>(this.documentService.getDocumentById(id), HttpStatus.OK);
    }

    @GetMapping("/doctor/{username}")
    public ResponseEntity<Set<Document>> getDoctorsDocuments(@PathVariable String username) {
        return new ResponseEntity<>(this.documentService.getDoctorsDocuments(username), HttpStatus.OK);
    }

    @GetMapping("/view/{id}")
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long id) throws MalformedURLException, FileNotFoundException {
        Resource file = this.documentService.loadDocumentAsResource(id);
        Document document = this.documentService.getDocumentById(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + document.getName() + "\"")
                .header(HttpHeaders.CONTENT_TYPE, document.getType())
                .body(file);
    }

    @PostMapping("/{username}/upload")
    public ResponseEntity<List<Document>> uploadFile(@PathVariable String username, @RequestParam("files") List<MultipartFile> files) throws IOException {
        return new ResponseEntity<>(this.documentService.saveDocument(files, username), HttpStatus.CREATED);
    }
}
