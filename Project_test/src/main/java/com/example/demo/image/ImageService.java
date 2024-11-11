package com.example.demo.image;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

public class ImageService {
	@Value("${itemimage.url}")
	private String imageUrl;
	@Autowired
	private ImageDao imageDao;
	
	public ImageService(ImageDao imageDao) {
		this.imageDao = imageDao;
	}
	
	public String saveImage(MultipartFile imageFile, Long itemNo, Long index) throws IOException {
		if (imageFile.isEmpty()) {
			return null;	// 이미지가 없으면 null 반환
		}
		
		String extension = FilenameUtils.getExtension(imageFile.getOriginalFilename());	// 확장자 추출
		String saveFilename = UUID.randomUUID().toString() + "." + extension;	// 고유한 파일명 생성
		
		File file = new File(imageUrl, saveFilename);	// 저장할 경로에 파일 생성
		imageFile.transferTo(file);	// 파일을 해당 경로에 저장
		
		imageDao.save(new ItemImage(itemNo, index, saveFilename));
		
		return saveFilename; // 저장된 파일이름 반환
	}
	
	public String getImageUrlByItemId(Long itemNo) {
		return imageDao.findImageUrlByItemId(itemNo);
	}
}
