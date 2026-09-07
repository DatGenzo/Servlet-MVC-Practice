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

    private static final String PROFILE_DIRECTORY =
            "profiles";

    private final Path uploadRoot;

    public LocalFileStorageService() {
        uploadRoot = ApplicationConfig
                .getUploadDirectory();
    }

    @Override
    public String saveCategoryIcon(Part iconPart) {
        return saveImage(
                iconPart,
                CATEGORY_DIRECTORY,
                AppConstants.MAX_CATEGORY_ICON_BYTES,
                "Icon"
        );
    }

    @Override
    public String saveProfileImage(
            int userId,
            Part imagePart
    ) {
        if (userId <= 0) {
            throw new ValidationException(
                    "Phiên đăng nhập không hợp lệ."
            );
        }

        return saveImage(
                imagePart,
                PROFILE_DIRECTORY + "/" + userId,
                AppConstants.MAX_PROFILE_IMAGE_BYTES,
                "Ảnh đại diện"
        );
    }

    private String saveImage(
            Part imagePart,
            String relativeDirectory,
            long maximumBytes,
            String fieldLabel
    ) {
        validatePart(
                imagePart,
                maximumBytes,
                fieldLabel
        );

        String extension =
                detectImageExtension(imagePart);

        Path imageDirectory =
                resolveSecurely(relativeDirectory);

        try {
            Files.createDirectories(imageDirectory);

            String generatedFileName =
                    UUID.randomUUID()
                            + "."
                            + extension;

            Path targetFile = imageDirectory
                    .resolve(generatedFileName)
                    .normalize();

            ensureInsideUploadRoot(targetFile);

            Path temporaryFile =
                    Files.createTempFile(
                            imageDirectory,
                            ".upload-",
                            ".tmp"
                    );

            try {
                copyPartToFile(
                        imagePart,
                        temporaryFile
                );

                moveSafely(
                        temporaryFile,
                        targetFile
                );
            } finally {
                Files.deleteIfExists(temporaryFile);
            }

            return relativeDirectory
                    + "/"
                    + generatedFileName;
        } catch (IOException exception) {
            throw new FileStorageException(
                    "Không thể lưu ảnh tải lên.",
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
                    "Không thể xóa ảnh đã lưu.",
                    exception
            );
        }
    }

    private void validatePart(
            Part imagePart,
            long maximumBytes,
            String fieldLabel
    ) {
        if (imagePart == null || imagePart.getSize() == 0) {
            throw new ValidationException(
                    "Vui lòng chọn "
                            + fieldLabel.toLowerCase(Locale.ROOT)
                            + "."
            );
        }

        if (imagePart.getSize() > maximumBytes) {
            throw new ValidationException(
                    fieldLabel + " không được vượt quá 2 MB."
            );
        }
    }

    private String detectImageExtension(Part imagePart) {
        try (
                InputStream inputStream =
                        imagePart.getInputStream();

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
                    "Không thể kiểm tra ảnh tải lên.",
                    exception
            );
        }
    }

    private void copyPartToFile(
            Part imagePart,
            Path destination
    ) throws IOException {

        try (InputStream inputStream =
                imagePart.getInputStream()) {

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
