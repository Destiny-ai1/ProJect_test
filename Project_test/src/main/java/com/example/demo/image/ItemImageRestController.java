package com.example.demo.image;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class ItemImageRestController {    
	// 이미지 저장 경로 수정 (ItemImageSaveLoad.IMAGE_FOLDER 사용)
	@PostMapping("/api/images")
	public ResponseEntity<?> itemImageUpload(MultipartFile upload) {
	    String originalFileName = upload.getOriginalFilename();
	    String extension = FilenameUtils.getExtension(originalFileName);
	    String saveImageName = UUID.randomUUID() + "." + extension;

	    // 실제 저장할 경로
	    File file = new File(ItemImageSaveLoad.IMAGE_FOLDER + saveImageName);
	    try {
	        upload.transferTo(file);
	        System.out.println("파일 저장 경로: " + file.getAbsolutePath());  // 파일 저장 경로 출력
	    } catch (IllegalStateException | IOException e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 업로드 실패");
	    }

	    // 클라이언트에 반환할 URL 경로 수정
	    String address = ItemImageSaveLoad.IMAGE_URL + saveImageName;
	    Map<String, Object> map = Map.of("uploaded", 1, "url", address, "filename", saveImageName);
	    return ResponseEntity.ok(map);
	}

	// 이미지 조회 경로 수정 (get으로 호출 시)
	 @GetMapping("/api/images")
	    public ResponseEntity<byte[]> viewImage(@RequestParam("imagename") String imageName) {
	        // 이미지 파일명이 비어있는 경우 기본 이미지 경로로 설정
	        if (imageName == null || imageName.isEmpty()) {
	            imageName = "normal/default-image.jpg"; // 기본 이미지 경로
	        }

	        try {
	            // 실제 파일 경로
	            Path imagePath = Paths.get(ItemImageSaveLoad.IMAGE_FOLDER + imageName);

	            // 파일이 존재하는지 확인
	            if (Files.notExists(imagePath)) {
	                // 파일이 존재하지 않으면 기본 이미지 사용
	                imagePath = Paths.get(ItemImageSaveLoad.IMAGE_FOLDER + "normal/default-image.jpg");
	            }

	            // 이미지 읽기
	            byte[] imageBytes = Files.readAllBytes(imagePath);

	            // mime 타입 확인
	            String mimeType = Files.probeContentType(imagePath);
	            MediaType mediaType = MediaType.parseMediaType(mimeType);

	            return ResponseEntity.status(HttpStatus.OK).contentType(mediaType).body(imageBytes);
	        } catch (IOException e) {
	            e.printStackTrace();
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	        }
	    }
}