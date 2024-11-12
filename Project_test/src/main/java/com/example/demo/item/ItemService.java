package com.example.demo.item;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.category.CategoryDao;
import com.example.demo.image.ImageDao;
import com.example.demo.image.ImageService;
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
	private ImageService imageService;
	@Autowired
	private ImageDao imageDao;

	
	// 카테고리 대분류
	public List<Map> findMajorCategory() {
		return categoryDao.findMajorCategory();
	}
	
	// 생성자 주입
	public ItemService(ItemDao itemDao) {
		this.itemDao = itemDao;
	}
	
	// 아이템 리스트
	public List<ItemDto.ItemList> findAll() {
		return itemDao.findAll(imageUrl);
	}
	
	// 상품 생성 서비스
	public void save(ItemDto.Create dto) {
		Item item  = dto.toEntity();					// item 정의
		itemDao.save(item);								// item에 dao정보를 삽입
		
		List<MultipartFile> images = dto.getImage();	// 생성되는 아이템에 원하는 이미지 불러오기
		
		// test필요
		// 상품에 따른 상품사진 개수 지정-사진이 없다면 건너뛰기
		for(long i=0; i<images.size(); i++) {
			try {																
				String savedImageFilename = imageService.saveImage(images.get((int) i), item.getItemNo(), i);
				// 저장된 이미지 파일 이름을 db에 저장하는 로직이 'imageService'에 존재
				if (savedImageFilename != null) {
					// 이미지 파일 이름을 db에 저장하는 작업을 추가
					imageDao.save(new ItemImage(item.getItemNo(), i, savedImageFilename)); // db에 저장
				}
			} catch (IOException e) {
				e.printStackTrace(); // 오류 발생시 예외처리
			}
		}
	}
	
	// 상품 번호를 이용하여 상품 이름을 조회
	public String getItemNameById(Long itemNo) {
		return itemDao.getItemNameById(itemNo);
	}
	
	// test 필요
	public ItemDto.Read read(Integer itemNo, String imageUrl) {					// 상품을 읽어오는 부분
		return itemDao.findById(itemNo, imageUrl);
	}
}
