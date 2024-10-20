package com.distributed.miraeasset.service;

import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ImageService {
    private String uploadFile(File file, String fileName, String contentType) throws IOException {
        BlobId blobId = BlobId.of("distributejava.appspot.com", "branch1/" + fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(contentType).build();
        InputStream inputStream = ImageService.class.getClassLoader().getResourceAsStream("distributejava-firebase-adminsdk-9l9a7-3010584d70.json");
        Credentials credentials = GoogleCredentials.fromStream(inputStream);
        Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();

        Blob blob = storage.create(blobInfo, Files.readAllBytes(file.toPath()));
        String token = UUID.randomUUID().toString();
        blob.toBuilder().setMetadata(Map.of("firebaseStorageDownloadTokens", token)).build().update();

        String DOWNLOAD_URL = "https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media&token=%s";

        return String.format(DOWNLOAD_URL, blob.getBucket(), URLEncoder.encode(fileName, StandardCharsets.UTF_8), token);
    }

    private File convertToFile(MultipartFile multipartFile, String fileName) throws IOException {
        File tempFile = new File(fileName);
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(multipartFile.getBytes());
            fos.close();
        }
        return tempFile;
    }

    public String upload(MultipartFile multipartFile) {
        try {
            String fileName = multipartFile.getOriginalFilename();

            File file = this.convertToFile(multipartFile, fileName);
            String contentType = multipartFile.getContentType();
            String URL = this.uploadFile(file, fileName, contentType);
            file.delete();
            return URL;
        } catch (Exception e) {
            e.printStackTrace();
            return "Image couldn't upload, Something went wrong";
        }
    }

    public List<String> uploadMultiple(MultipartFile[] multipartFiles) {
        List<String> urls = new ArrayList<>();
        for (MultipartFile file : multipartFiles) {
            String url = this.upload(file);
            urls.add(url);
        }
        return urls;
    }
}
