package com.thanhdat.servletmvc.services;

import java.nio.file.Path;
import java.util.Optional;

import jakarta.servlet.http.Part;

public interface FileStorageService {

    String saveCategoryIcon(Part iconPart);

    Optional<Path> find(String relativePath);

    void delete(String relativePath);
}