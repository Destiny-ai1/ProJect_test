package com.example.demo.image;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ItemImageUtil {
	@Value("${itemimage.upload.path}")
	private String imageUploadPath;
	
	// Jsoup을 이용해 글 본문에서 이미지 이름들을 추출하는 함수
	public List<String> getImageName(String html) {
		// Jsoup을 이용해 html을 파싱
		Document document = Jsoup.parse(html);
		Elements elements = document.select("img");
		
		List<String> result = new ArrayList<>();
		for(Element element:elements) {
			String src = element.attr("src");
			int equalsPosition = src.indexOf("=");
			String imagename = src.substring(equalsPosition+1);
			System.out.println(imagename);
		}
		return result;
	}
	
	public ResponseEntity<byte[]> getImageResponseEntity(String image_name) {
		try {
			Path filePath = Paths.get(imageUploadPath + image_name);
			byte[] imageBytes = Files.readAllBytes(filePath);
			String mimeType = Files.probeContentType(filePath);
			
			MediaType mediaType = MediaType.parseMediaType(mimeType);
			return ResponseEntity.status(HttpStatus.OK).contentType(mediaType).body(imageBytes);
		} catch(IOException e) {
			e.printStackTrace();
		}
		return ResponseEntity.status(409).body(null);
	}
}
