package com.example.demo.item;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.category.CategoryDao;
import com.example.demo.image.ItemImage;
import com.example.demo.image.ItemImageDao;
import com.example.demo.image.ItemImageSaveLoad;
import com.example.demo.item.ItemDto.Read;

@Service
@Transactional
public class ItemService {
	@Autowired
	private ItemDao itemDao;
	@Autowired
	private CategoryDao categoryDao;
	@Autowired
	private ItemImageDao imageDao;
	
	// 카테고리 대분류 검색
	public List<Map> findMajorCategory() {
		return categoryDao.findMajorCategory();
	}
	
	// 전체 상품 검색
	public List<ItemDto.ItemList> findAll(String imageUrl) {
	    if (imageUrl == null || imageUrl.trim().isEmpty()) {
	        imageUrl = "/default/images/"; // 기본 이미지 URL을 설정 (예시)
	    }
	    // 모든 상품을 조회한 후, 각 상품의 평균 평점을 포함시킴
	    List<ItemDto.ItemList> items = itemDao.findAll(imageUrl);

	    for (ItemDto.ItemList item : items) {
	        // 각 상품의 평균 평점 계산
	        Double avgRating = itemDao.findAverageRatingByItemNo(item.getItemNo());
	        item.setAvgRating(avgRating); // 평점 추가
	    }

	    return items;
	}
	
	// 추가할 상품 정보를 저장
	@Transactional
	public void save(ItemDto.Create dto) {
		// 1. 상품 정보를 DB에 저장
		Item item = dto.toEntity();
		itemDao.save(item);

		Long itemNo = item.getItemNo();
		if (itemNo == null) {
			throw new RuntimeException("상품 저장 실패: itemNo가 생성되지 않았습니다.");
		}

		// 2. 이미지 저장
		List<MultipartFile> images = dto.getItemImages();
		if (images == null || images.isEmpty()) {
			images = new ArrayList<>();
		}

		for (long i = 0; i < images.size(); i++) {
			MultipartFile imageFile = images.get((int) i);
			if (imageFile.isEmpty()) {
				continue;
			}

			// 파일 확장자 추출
			String extension = FilenameUtils.getExtension(imageFile.getOriginalFilename());
			String saveFilename = UUID.randomUUID().toString() + "." + extension;

			// 저장할 파일 경로 지정
			File file = new File(ItemImageSaveLoad.IMAGE_FOLDER, saveFilename);
			try {
				// 이미지 파일을 실제 디스크에 저장
				imageFile.transferTo(file);

				// item_image 테이블에 이미지 저장
				ItemImage itemImage = new ItemImage(null, itemNo, saveFilename);
				imageDao.save(itemImage);
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException("이미지 저장 중 오류 발생", e);
			}
		}
	}

	// 상품 번호에 해당하는 이미지 정보 삭제
	// 상품 정보 삭제
	@Transactional
	public boolean deleteItem(Long itemNo) {
		// 1. 상품에 관련된 이미지들 조회 (item_image 테이블에서 해당 itemNo로 이미지 찾기)
		List<ItemImage> itemImages = itemDao.findByItemNo(itemNo); // imageDao가 아니라 itemDao에서 findByItemNo 호출

		// 2. 이미지 파일 시스템에서 삭제
		for (ItemImage itemImage : itemImages) {
			String imagePath = ItemImageSaveLoad.IMAGE_FOLDER + itemImage.getImageName();
			File imageFile = new File(imagePath);

			if (imageFile.exists()) {
				boolean isDeleted = imageFile.delete(); // 실제 파일 삭제
				if (!isDeleted) {
					throw new RuntimeException("이미지 파일 삭제 실패: " + imagePath);
				}
			}
		}

		// 3. DB에서 이미지 정보 삭제 (item_image 테이블에서 해당 itemNo에 맞는 이미지 삭제)
		itemDao.deleteItemImageByItemNo(itemNo); // imageDao가 아니라 itemDao의 deleteItemImageByItemNo 호출

		// 4. 상품 정보 삭제
		itemDao.deleteItemByItemNo(itemNo); // 상품 삭제 (상품 테이블에서 itemNo로 삭제)

		return true; // 삭제 성공 시 true 반환
	}

	// 상품 상세 조회
	public ItemDto.Read read(Long itemNo, String imageUrl) {
		if (imageUrl == null || imageUrl.trim().isEmpty()) {
			imageUrl = "/api/images?imagename="; // 기본 이미지 URL
		}

		// DB에서 상품 정보를 조회하며, 이미지 URL도 함께 가져옵니다.
		ItemDto.Read itemDto = itemDao.findById(itemNo, imageUrl);

		// 만약 아이템에 이미지가 없다면 기본 이미지 경로 설정
		if (itemDto != null && (itemDto.getItemImages() == null || itemDto.getItemImages().isEmpty())) {
			itemDto.setItemImages(List.of("normal/default-image.jpg"));
		}

		// 재고가 10 미만인 경우 남은 수량 메시지 설정
		if (itemDto != null && itemDto.getItemJango() != null && itemDto.getItemJango() < 10) {
			itemDto.setStockMessage("남은 상품 수량: " + itemDto.getItemJango());
		} else if (itemDto != null) {
			itemDto.setStockMessage(null);
		}

		// 평균 평점 계산
		Double avgRating = itemDao.findAverageRatingByItemNo(itemNo);
		itemDto.setAvgRating(avgRating); // 평점 추가

		return itemDto;
	}

	public List<ItemDto.ItemList> findItemsByCategory(Long cno, String imageUrl) {
	    if (imageUrl == null || imageUrl.trim().isEmpty()) {
	        imageUrl = "/api/images?imagename=";  // 기본 이미지 URL
	    }
	    
	    // 카테고리별 상품 목록 조회
	    List<ItemDto.ItemList> items = itemDao.findItemsByCategory(cno, imageUrl);

	    // 상품 번호를 기준으로 중복 제거
	    Map<Long, ItemDto.ItemList> uniqueItems = new LinkedHashMap<>();
	    
	    for (ItemDto.ItemList item : items) {
	        if (!uniqueItems.containsKey(item.getItemNo())) {
	            uniqueItems.put(item.getItemNo(), item);
	        } else {
	            // 기존 항목에 이미지를 추가
	            uniqueItems.get(item.getItemNo()).setItemImage(item.getItemImage());
	        }
	    }
	    
	    // 중복 제거된 상품 목록
	    List<ItemDto.ItemList> itemList = new ArrayList<>(uniqueItems.values());

	    // 각 상품에 대해 평균 평점 계산
	    for (ItemDto.ItemList item : itemList) {
	        // 상품 번호를 기준으로 평균 평점 조회
	        Double avgRating = itemDao.findAverageRatingByItemNo(item.getItemNo());
	        // 평점이 null이면 '평점 없음' 혹은 0.0 처리
	        item.setAvgRating(avgRating != null ? avgRating : 0.0);
	    }
	    
	    return itemList;
	}
	
	// 상품 번호로 전체 상품 정보를 반환하는 메소드
	public ItemDto.Read getItemByNo(Long itemNo, String language) {
	    System.out.println("Fetching item by itemNo: " + itemNo);
	    // 실제 상품 조회 로직
	    ItemDto.Read item = (Read) itemDao.findByItemNo(itemNo);
	    System.out.println("Item found: " + item);
	    return item;
	}

}
