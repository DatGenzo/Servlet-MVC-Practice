package com.thanhdat.servletmvc.utils;

import jakarta.servlet.http.Part;

public final class MultipartUtils {

    private MultipartUtils() {
    }

    public static boolean hasUploadedFile(Part part) {
        return part != null
                && part.getSize() > 0
                && part.getSubmittedFileName() != null
                && !part.getSubmittedFileName().isBlank();
    }
}