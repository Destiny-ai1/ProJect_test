package com.example.demo.image;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.FileSystemResource;
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
	// 이미지 저장
	@PostMapping("/api/images")
	public ResponseEntity<?> itemImageUpload(MultipartFile upload) {
		// 기존 파일 이름
	    String originalFileName = upload.getOriginalFilename();
	    // 확장자명 설정
	    String extension = FilenameUtils.getExtension(originalFileName);
	    // 랜덤한 이름으로 이미지 저장
	    String saveImageName = UUID.randomUUID() + "." + extension;

	    // 실제 저장할 경로
	    File file = new File(ItemImageSaveLoad.IMAGE_FOLDER + saveImageName);
	    try {
	        upload.transferTo(file);
	        System.out.println("파일 저장 경로: " + file.getAbsolutePath());
	    } catch (IllegalStateException | IOException e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 업로드 실패");
	    }

	    // 클라이언트에 반환할 URL 경로 수정
	    String address = ItemImageSaveLoad.IMAGE_URL + saveImageName;
	    Map<String, Object> map = Map.of("uploaded", 1, "url", address, "filename", saveImageName);
	    return ResponseEntity.ok(map);
	}

	// 저장한 이미지 적절히 노출
    @GetMapping("/api/images")
    public ResponseEntity<byte[]> viewImage(@RequestParam("imagename") String imageName) {
        if (imageName == null || imageName.isEmpty()) {
            imageName = "normal/default-image.jpg"; // 기본 이미지 경로
        }

        try {
            // URL 디코딩 처리: URL에서 전달된 파일 이름이 URL 인코딩되어 있을 수 있음
            String decodedImageName = java.net.URLDecoder.decode(imageName, "UTF-8");
            
            // 경로를 안전하게 결합 (기본 경로 + 이미지 이름)
            Path imagePath = Paths.get(ItemImageSaveLoad.IMAGE_FOLDER + decodedImageName);
            
            // 이미지 파일이 없으면 기본 이미지로 대체
            if (Files.notExists(imagePath)) {
                imagePath = Paths.get(ItemImageSaveLoad.IMAGE_FOLDER + "normal/default-image.jpg");
            }

            byte[] imageBytes = Files.readAllBytes(imagePath);

            // MIME 타입을 추출하고 MediaType으로 변환
            String mimeType = Files.probeContentType(imagePath);
            MediaType mediaType = MediaType.parseMediaType(mimeType);

            return ResponseEntity.status(HttpStatus.OK).contentType(mediaType).body(imageBytes);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}
