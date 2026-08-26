package com.thanhdat.servletmvc.services.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Iterator;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import com.thanhdat.servletmvc.config.ApplicationConfig;
import com.thanhdat.servletmvc.exceptions.FileStorageException;
import com.thanhdat.servletmvc.exceptions.ValidationException;
import com.thanhdat.servletmvc.services.FileStorageService;
import com.thanhdat.servletmvc.utils.AppConstants;

import jakarta.servlet.http.Part;

public class LocalFileStorageService
        implements FileStorageService {

    private static final String CATEGORY_DIRECTORY =
            "categories";

    private final Path uploadRoot;

    public LocalFileStorageService() {
        uploadRoot = ApplicationConfig
                .getUploadDirectory();
    }

    @Override
    public String saveCategoryIcon(Part iconPart) {
        validatePart(iconPart);

        String extension =
                detectImageExtension(iconPart);

        Path categoryDirectory =
                resolveSecurely(CATEGORY_DIRECTORY);

        try {
            Files.createDirectories(categoryDirectory);

            String generatedFileName =
                    UUID.randomUUID()
                            + "."
                            + extension;

            Path targetFile = categoryDirectory
                    .resolve(generatedFileName)
                    .normalize();

            ensureInsideUploadRoot(targetFile);

            Path temporaryFile =
                    Files.createTempFile(
                            categoryDirectory,
                            ".upload-",
                            ".tmp"
                    );

            try {
                copyPartToFile(
                        iconPart,
                        temporaryFile
                );

                moveSafely(
                        temporaryFile,
                        targetFile
                );
            } finally {
                Files.deleteIfExists(temporaryFile);
            }

            return CATEGORY_DIRECTORY
                    + "/"
                    + generatedFileName;
        } catch (IOException exception) {
            throw new FileStorageException(
                    "Không thể lưu icon Category.",
                    exception
            );
        }
    }

    @Override
    public Optional<Path> find(String relativePath) {
        if (relativePath == null
                || relativePath.isBlank()) {
            return Optional.empty();
        }

        Path file = resolveSecurely(relativePath);

        if (!Files.isRegularFile(file)) {
            return Optional.empty();
        }

        return Optional.of(file);
    }

    @Override
    public void delete(String relativePath) {
        if (relativePath == null
                || relativePath.isBlank()) {
            return;
        }

        Path file = resolveSecurely(relativePath);

        try {
            Files.deleteIfExists(file);
        } catch (IOException exception) {
            throw new FileStorageException(
                    "Không thể xóa icon Category.",
                    exception
            );
        }
    }

    private void validatePart(Part iconPart) {
        if (iconPart == null || iconPart.getSize() == 0) {
            throw new ValidationException(
                    "Vui lòng chọn icon."
            );
        }

        if (iconPart.getSize()
                > AppConstants.MAX_CATEGORY_ICON_BYTES) {
            throw new ValidationException(
                    "Icon không được vượt quá 2 MB."
            );
        }
    }

    private String detectImageExtension(Part iconPart) {
        try (
                InputStream inputStream =
                        iconPart.getInputStream();

                ImageInputStream imageInputStream =
                        ImageIO.createImageInputStream(
                                inputStream
                        )
        ) {
            if (imageInputStream == null) {
                throw new ValidationException(
                        "File tải lên không phải ảnh hợp lệ."
                );
            }

            Iterator<ImageReader> readers =
                    ImageIO.getImageReaders(
                            imageInputStream
                    );

            if (!readers.hasNext()) {
                throw new ValidationException(
                        "File tải lên không phải ảnh hợp lệ."
                );
            }

            ImageReader reader = readers.next();

            try {
                String formatName =
                        reader.getFormatName()
                                .toLowerCase(Locale.ROOT);

                return switch (formatName) {
                    case "jpeg", "jpg" -> "jpg";
                    case "png" -> "png";
                    default ->
                            throw new ValidationException(
                                    "Chỉ chấp nhận ảnh JPG hoặc PNG."
                            );
                };
            } finally {
                reader.dispose();
            }
        } catch (IOException exception) {
            throw new FileStorageException(
                    "Không thể kiểm tra icon tải lên.",
                    exception
            );
        }
    }

    private void copyPartToFile(
            Part iconPart,
            Path destination
    ) throws IOException {

        try (InputStream inputStream =
                iconPart.getInputStream()) {

            Files.copy(
                    inputStream,
                    destination,
                    StandardCopyOption.REPLACE_EXISTING
            );
        }
    }

    private void moveSafely(
            Path source,
            Path target
    ) throws IOException {

        try {
            Files.move(
                    source,
                    target,
                    StandardCopyOption.ATOMIC_MOVE
            );
        } catch (AtomicMoveNotSupportedException exception) {
            Files.move(
                    source,
                    target,
                    StandardCopyOption.REPLACE_EXISTING
            );
        }
    }

    private Path resolveSecurely(String relativePath) {
        Path resolvedPath = uploadRoot
                .resolve(relativePath)
                .toAbsolutePath()
                .normalize();

        ensureInsideUploadRoot(resolvedPath);

        return resolvedPath;
    }

    private void ensureInsideUploadRoot(Path path) {
        if (!path.startsWith(uploadRoot)) {
            throw new ValidationException(
                    "Đường dẫn file không hợp lệ."
            );
        }
    }
}