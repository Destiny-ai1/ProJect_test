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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class ImageRestController {
	@Value("${itemimage.upload.path}")
	private String imageFolder;
	@Value("${itemimage.url}")
	private String imageUrl;
	
	@PostMapping("/api/images")
	public ResponseEntity<?> itemImageUpload(MultipartFile upload) {
		String originalFileName = upload.getOriginalFilename();
		String extension = FilenameUtils.getExtension(originalFileName);
		String saveImageName = UUID.randomUUID() + "." + extension;
		
		File file = new File(imageFolder + saveImageName);
		
		// 경로가 없으면 생성
	    if (!file.getParentFile().exists()) {
	        file.getParentFile().mkdirs();  // 경로 생성
	    }
		
		try {
			upload.transferTo(file);
			System.out.println("파일 저장 경로: " + file.getAbsolutePath());  // 파일 저장 경로 출력
		} catch (IllegalStateException | IOException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 업로드 실패");
		}
		
		String address = imageUrl + saveImageName;
		Map<String, Object> map = Map.of("uploaded",1,"url",address,"filename", saveImageName);
		return ResponseEntity.ok(map);
	}
	
	@GetMapping("/api/images")
	public ResponseEntity<byte[]> viewImage(String imagename) {
		try {
			Path imagePath = Paths.get(imageFolder + imagename);
			byte[] imageBytes = Files.readAllBytes(imagePath);
			String mimeType = Files.probeContentType(imagePath);
			MediaType mediaType = MediaType.parseMediaType(mimeType);
			return ResponseEntity.ok().contentType(mediaType).body(imageBytes);
		} catch (IOException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
		}
	}
}
