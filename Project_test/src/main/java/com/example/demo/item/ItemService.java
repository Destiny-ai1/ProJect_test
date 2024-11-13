package com.example.demo.item;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.category.CategoryDao;
import com.example.demo.image.ImageDao;
import com.example.demo.image.ItemImage;

@Service
public class ItemService {
	@Value("${itemimage.url}")
	private String imageUrl;
	@Autowired
	private ItemDao itemDao;
	@Autowired
	private CategoryDao categoryDao;
	@Autowired
	private ImageDao imageDao;

	// 생성자 주입
	public ItemService(ItemDao itemDao) {
		this.itemDao = itemDao;
	}
	
	// 카테고리 대분류
	public List<Map> findMajorCategory() {
		return categoryDao.findMajorCategory();
	}
	
	// 아이템 리스트
	public List<ItemDto.ItemList> findAll() {
		return itemDao.findAll(imageUrl);
	}
	
	// 상품 생성 서비스
	public void save(ItemDto.Create dto) {
        Item item = dto.toEntity();  // ItemDto.Create를 Item으로 변환
        itemDao.save(item);          // item을 DB에 저장

        List<MultipartFile> images = dto.getImage();  // 이미지 목록

        if (images == null || images.isEmpty()) {
            images = new ArrayList<>();
        }

        // 상품에 대한 이미지를 처리
        for (Long i = 0L; i < images.size(); i++) {
        	int index = i.intValue();
            MultipartFile imageFile = images.get(index);
            if (imageFile.isEmpty()) {
                continue;  // 이미지 파일이 비어있으면 건너뛰기
            }

            String extension = FilenameUtils.getExtension(imageFile.getOriginalFilename());
            String saveFilename = UUID.randomUUID().toString() + "." + extension;

            File file = new File(imageUrl, saveFilename);
            try {
                imageFile.transferTo(file);  // 실제 파일 저장
                imageDao.save(new ItemImage(item.getItemNo(), i, saveFilename));  // DB에 이미지 정보 저장
            } catch (IOException e) {
                e.printStackTrace();  // 예외 처리
			}
		}	
	}
	
	// 상품 번호를 이용하여 상품 이름을 조회
	public String getItemNameById(Long itemNo) {
		return itemDao.getItemNameById(itemNo);
	}
	
	// 상품 번호로 상품 읽기
	public ItemDto.Read read(Long itemNo, String imageUrl) {				
		return itemDao.findById(itemNo, imageUrl);
	}
}
