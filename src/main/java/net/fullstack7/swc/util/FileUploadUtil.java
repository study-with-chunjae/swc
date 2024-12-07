package net.fullstack7.swc.util;

import lombok.extern.log4j.Log4j2;
import net.fullstack7.swc.constant.FileConstants;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Component
@Log4j2
public class FileUploadUtil {
  private static final String UPLOAD_PATH = "/upload/";

  @Value("${file.upload.path}")
  private String uploadPath;

  /**
   * 단일 파일 업로드
   */
  public String uploadFile(MultipartFile file, String subPath) throws IOException {
    if (file.isEmpty()) {
      log.info("파일이 비어있습니다.");
      throw new IllegalArgumentException("파일이 비어있습니다.");
    }

    String originalFilename = file.getOriginalFilename();
    log.info("파일 업로드: {}", originalFilename);

    try {
      // 파일명 생성
      String extension = FilenameUtils.getExtension(originalFilename);
      String newFilename = UUID.randomUUID().toString() + "." + extension;

      // 업로드 경로 생성
      String targetPath = Paths.get(uploadPath, subPath).toString();
      File targetDir = new File(targetPath);
      if (!targetDir.exists()) {
        targetDir.mkdirs();
        log.debug("디렉토리 생성: {}", targetPath);
      }

      // 파일 저장
      Path targetFile = Paths.get(targetPath, newFilename);
      Files.copy(file.getInputStream(), targetFile, StandardCopyOption.REPLACE_EXISTING);

      log.info("파일 업로드 성공: {}", newFilename);
      return UPLOAD_PATH + Paths.get(subPath, newFilename).toString();

    } catch (IOException e) {
      log.error("파일 업로드 실패: {}", originalFilename, e);
      throw e;
    }
  }

  /**
   * 다중 파일 업로드
   */
  public List<String> uploadFiles(List<MultipartFile> files, String subPath) {
    log.info("{}개의 파일 업로드: {}", files.size(), subPath);
    List<String> uploadedFiles = new ArrayList<>();

    for (MultipartFile file : files) {
      try {
        String filePath = uploadFile(file, subPath);
        uploadedFiles.add(filePath);
      } catch (IOException e) {
        log.error("파일 업로드 실패: {}", file.getOriginalFilename(), e);
      }
    }

    return uploadedFiles;
  }

  /**
   * 파일 삭제
   */
  public void deleteFile(String filePath) {
    if (filePath == null) {
        return;
    }
    try {
        String relativePath = filePath.replace(UPLOAD_PATH, "");
        Path path = Paths.get(uploadPath, relativePath);
        log.debug("Deleting file: {}", path.toAbsolutePath());
        
        if (Files.exists(path)) {
            Files.delete(path);
            log.info("파일 삭제 완료: {}", filePath);
        } else {
            log.warn("삭제할 파일이 존재하지 않습니다: {}", filePath);
        }
    } catch (IOException e) {
        log.error("파일 삭제 중 오류 발생: {}", filePath, e);
        throw new RuntimeException("파일 삭제 실패", e);
    }
  }

  /**
   * 파일 존재 여부 확인
   */
  public boolean exists(String filePath) {
    if (filePath == null) {
      return false;
    }

    // /uploads/documents/file.pdf -> documents/file.pdf
    String relativePath = filePath.replace(UPLOAD_PATH, "");
    Path fullPath = Paths.get(uploadPath, relativePath);
    log.debug("Checking file at: {}", fullPath.toAbsolutePath());
    return Files.exists(fullPath);
  }

  /**
   * 파일 경로 가져오기
   */
  public Path getFilePath(String filePath) {
    String relativePath = filePath.replace(UPLOAD_PATH, "");
    Path fullPath = Paths.get(uploadPath, relativePath);
    log.debug("Full file path: {}", fullPath.toAbsolutePath());
    return fullPath;
  }

  public void validateFileExtension(MultipartFile file, String[] allowedExtensions) {
    String extension = FilenameUtils.getExtension(file.getOriginalFilename()).toLowerCase();
    LogUtil.log("extension",extension);
    boolean isValid = Arrays.asList(allowedExtensions).contains(extension);

    if (!isValid) {
      throw new IllegalArgumentException("허용되지 않는 파일 형식입니다: " + extension);
    }
  }

//  public String uploadVideoFile(MultipartFile file, String subPath) throws IOException {
//    validateFileExtension(file, FileConstants.ALLOWED_VIDEO_EXTENSIONS);
//    return uploadFile(file, subPath);
//  }

  public String uploadDocumentFile(MultipartFile file, String subPath) throws IOException {
    validateFileExtension(file, FileConstants.ALLOWED_DOCUMENT_EXTENSIONS);
    return uploadFile(file, subPath);
  }

  public String uploadImageFile(MultipartFile file, String subPath) throws IOException {
    validateFileExtension(file, FileConstants.ALLOWED_IMAGE_EXTENSIONS);
    return uploadFile(file, subPath);
  }

  /**
   * 파일 다운로드를 위한 Resource 생성
   */
  public ResponseEntity<Resource> downloadFile(String filePath) {
    log.info("파일 다운로드 요청: {}", filePath);
    try {
      // 파일 존재 여부 확인
      if (!exists(filePath)) {
        log.error("파일을 찾을 수 없습니다: {}", filePath);
        return ResponseEntity.notFound().build();
      }

      // 파일 리소스 생성
      Path path = getFilePath(filePath);
      Resource resource = new UrlResource(path.toUri());

      // 파일명 추출 및 인코딩
      String filename = path.getFileName().toString();
      String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8.toString())
          .replaceAll("\\+", "%20");

      // Content-Type 설정
      String contentType = determineContentType(path);

      log.info("파일 다운로드 처리: {} ({})", filename, contentType);

      return ResponseEntity.ok()
          .contentType(MediaType.parseMediaType(contentType))
          .header(HttpHeaders.CONTENT_DISPOSITION,
              "attachment; filename=\"" + encodedFilename + "\"")
          .body(resource);

    } catch (Exception e) {
      log.error("파일 다운로드 중 오류 발생", e);
      e.printStackTrace(); // 상세 에러 확인을 위해 추가
      return ResponseEntity.internalServerError().build();
    }
  }

  /**
   * 파일 확장자에 따른 Content-Type 결정
   * 1. 브라우저의 동작 결정
   * 2. 보안
   * 3. 사용자 경험
   * 4. 캐싱
   */
  private String determineContentType(Path path) {
    try {
      String contentType = Files.probeContentType(path);
      return contentType != null ? contentType : "application/octet-stream";
    } catch (IOException e) {
      log.warn("Content-Type 결정 실패, 기본값 사용: {}", path.getFileName());
      return "application/octet-stream";
    }
  }
}