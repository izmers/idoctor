package com.se.idoctor.service;

import com.se.idoctor.entity.Doctor;
import com.se.idoctor.entity.Document;
import com.se.idoctor.exception.EntityNotFoundException;
import com.se.idoctor.repository.DocumentRepository;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@Service
public class DocumentServiceImpl implements DocumentService {
    private final Path rootLocation = Paths.get("doctor-documents");
    private DocumentRepository documentRepository;
    private DoctorService doctorService;
    private UserService userService;


    @Override
    public List<Document> saveDocument(List<MultipartFile> files, String username) throws IOException {
        List<Document> documents = new ArrayList<>();
        Doctor doctor = this.doctorService.getDoctorByUsername(username);

        for (MultipartFile file : files) {
            String fileName = username + "_" + file.getOriginalFilename();

            Path destinationFile = rootLocation.resolve(fileName).normalize();
            Files.copy(file.getInputStream(), destinationFile);

            Document document = new Document();
            document.setName(fileName);
            document.setType(file.getContentType());
            document.setFilePath(destinationFile.toString());
            document.setDoctor(doctor);
            documents.add(document);
        }

        return (List<Document>) this.documentRepository.saveAll(documents);
    }

    @Override
    public Document getDocumentById(Long id) {
        return this.documentRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(Document.class, id));
    }

    @Override
    public Set<Document> getDoctorsDocuments(String username) {
        Doctor doctor = this.userService.getUserByUsername(username).getDoctor();
        return doctor.getDocuments();
    }

    @Override
    public Resource loadDocumentAsResource(Long id) throws MalformedURLException, FileNotFoundException {
        String fileName = this.getDocumentById(id).getName();
        Path filePath = rootLocation.resolve(fileName).normalize();
        Resource resource = new UrlResource(filePath.toUri());
        if (resource.exists()) {
            return resource;
        } else {
            throw new FileNotFoundException("File not found: " + fileName);
        }
    }
}
