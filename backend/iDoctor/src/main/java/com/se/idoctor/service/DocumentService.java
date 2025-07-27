package com.se.idoctor.service;

import com.se.idoctor.entity.Document;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Set;

public interface DocumentService {
    List<Document> saveDocument(List<MultipartFile> files, String username) throws IOException;
    Document getDocumentById(Long id);
    Set<Document> getDoctorsDocuments(String username) ;
    Resource loadDocumentAsResource(Long id) throws MalformedURLException, FileNotFoundException;
}
