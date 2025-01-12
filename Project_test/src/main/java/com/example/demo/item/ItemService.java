package com.example.demo.item;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.cart.CartDao;
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
	@Autowired
	private CartDao cartDao;

	// 상품 사이즈별 재고 정보를 item_size 테이블에 저장
	private void saveItemSizes(Long itemNo, List<ItemDto.Create.ItemSizeDto> itemSizes) {
		if (itemSizes != null && !itemSizes.isEmpty()) {
			for (ItemDto.Create.ItemSizeDto itemSize : itemSizes) {
				itemDao.saveItemSize(itemNo, itemSize.getItemSize(), itemSize.getItemJango());
			}
		}
	}

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

			// 재고 상태 메시지 설정
			if (item.getItemJango() != null && item.getItemJango() < 10) {
				item.setStockMessage("남은 상품 수량: " + item.getItemJango());
			} else {
				item.setStockMessage(null); // 재고가 충분하면 메시지 제거
			}
		}

		return items;
	}

	@Transactional
	public void save(ItemDto.Create dto) {
	    // 1. DTO에서 엔티티로 변환
	    Item item = dto.toEntity();

	    // 2. itemSellQty와 reviewEa가 null일 경우 0으로 설정
	    item.updateSellQtyAndReview(dto.getItemSellQty(), dto.getReviewEa());

	    // 3. 상품 정보를 DB에 저장
	    itemDao.save(item); // DB에 item 저장

	    Long itemNo = item.getItemNo();
	    if (itemNo == null) {
	        throw new RuntimeException("상품 저장 실패: itemNo가 생성되지 않았습니다.");
	    }

	    // 4. 이미지 저장
	    List<MultipartFile> images = dto.getItemImages();
	    if (images == null || images.isEmpty()) {
	        images = new ArrayList<>();
	    }

	    // 이미지 저장 처리
	    for (MultipartFile imageFile : images) {
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
	            imageDao.save(itemImage); // 이미지 정보 DB에 저장
	        } catch (IOException e) {
	            e.printStackTrace();
	            throw new RuntimeException("이미지 저장 중 오류 발생", e);
	        }
	    }

	    // 5. 상품 사이즈별 재고 정보 저장
	    saveItemSizes(itemNo, dto.getItemSizes());
	}



	// 상품 번호에 해당하는 이미지 정보 삭제
	// 상품 정보 삭제
	 @Transactional
	    public boolean deleteItem(Long itemNo) {
	        try {
	        	// 0. 리뷰에서 해당하는 리뷰 삭제
	        	itemDao.deletereviewByItemNo(itemNo);	// 해당 상품번호의 리뷰 모두 삭제
	        	
	            // 1. 장바구니에서 해당 상품 삭제
	            itemDao.deleteFromCartByItemNo(itemNo);  // 장바구니에서 모든 사용자의 해당 상품 삭제

	            // 2. 상품 사이즈 정보 삭제 (item_size 테이블에서 해당 itemNo에 맞는 사이즈 정보 삭제)
	            itemDao.deleteItemSizeByItemNo(itemNo);

	            // 3. 상품에 관련된 이미지들 조회 (item_image 테이블에서 해당 itemNo로 이미지 찾기)
	            List<ItemImage> itemImages = itemDao.findByItemNo(itemNo); // itemDao에서 findByItemNo 호출

	            // 4. 이미지 파일 시스템에서 삭제
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

	            // 5. DB에서 이미지 정보 삭제 (item_image 테이블에서 해당 itemNo에 맞는 이미지 삭제)
	            itemDao.deleteItemImageByItemNo(itemNo); // item_image 테이블에서 이미지 삭제

	            // 6. 상품 정보 삭제 (item 테이블에서 itemNo로 삭제)
	            itemDao.deleteItemByItemNo(itemNo); // 상품 삭제 (상품 테이블에서 itemNo로 삭제)

	            return true; // 삭제 성공 시 true 반환
	        } catch (Exception e) {
	            // 예외 발생 시 롤백 처리 (트랜잭션)
	            throw new RuntimeException("상품 삭제 실패", e);
	        }
	    }


	 // itemService의 read 메소드
	 public ItemDto.Read read(Long itemNo, String imageUrl, String itemSize) {
	     // 이미지 URL이 비어있으면 기본 이미지 URL을 설정
	     if (imageUrl == null || imageUrl.trim().isEmpty()) {
	         imageUrl = "/api/images?imagename="; // 기본 이미지 URL
	     }

	     // DB에서 상품 정보를 조회하며, 이미지 URL도 함께 가져옵니다.
	     ItemDto.Read itemDto = itemDao.findById(itemNo, imageUrl);

	     // 만약 아이템에 이미지가 없다면 기본 이미지 경로 설정
	     if (itemDto != null && (itemDto.getItemImages() == null || itemDto.getItemImages().isEmpty())) {
	         itemDto.setItemImages(List.of("normal/default-image.jpg")); // 기본 이미지 설정
	     }

	     // 평균 평점 계산 (null이면 0으로 설정)
	     Double avgRating = itemDao.findAverageRatingByItemNo(itemNo);
	     itemDto.setAvgRating(avgRating != null ? avgRating : 0.0); // 평균 평점이 없으면 0으로 설정

	     // `itemInfo` 줄바꿈을 <br> 태그로 변환하여 처리
	     if (itemDto != null) {
	         String itemInfo = itemDto.getItemInfo();
	         
	         // 줄바꿈을 <br> 태그로 변환
	         if (itemInfo != null) {
	             itemInfo = itemInfo.replace("\n", "<br>");  // 줄바꿈을 <br> 태그로 변환

	             // 중복된 <p> 태그 제거
	             itemInfo = itemInfo.replaceAll("(<p>\\s*)+","<p>").replaceAll("(\\s*</p>)+", "</p>");

	             itemDto.setItemInfo(itemInfo);  // 변환된 itemInfo 설정
	         }
	     }

	     // 사이즈 선택이 있을 경우, 해당 사이즈에 대한 재고 상태 확인
	     if (itemDto != null && itemSize != null && !itemSize.trim().isEmpty()) {
	         String stockMessage = getStockMessage(itemNo, itemSize);
	         itemDto.setStockMessage(stockMessage); // 사이즈별 재고 메시지 설정
	     }
	     return itemDto;
	 }

	// 재고 수량이 10개 미만이면 메시지 출력
	public String getStockMessage(Long itemNo, String itemSize) {
		// 해당 상품과 사이즈에 대한 재고 수량을 조회
		Integer stock = itemDao.getStockByItemSize(itemNo, itemSize);

		// 재고가 10 미만인 경우 메시지 반환
		if (stock != null && stock < 10) {
			return "남은 상품 수량: " + stock;
		} else {
			return null; // 재고가 충분하면 메시지 없음
		}
	}

	public List<ItemDto.ItemList> findItemsByCategory(Long cno, String imageUrl, Long excludeCno) {
	    if (imageUrl == null || imageUrl.trim().isEmpty()) {
	        imageUrl = "/api/images?imagename="; // 기본 이미지 URL
	    }

	    // 카테고리별 상품 목록 조회
	    List<ItemDto.ItemList> items = itemDao.findItemsByCategory(cno, imageUrl);

	    // 'excludeCno'가 주어졌다면 해당 카테고리 제외
	    if (excludeCno != null) {
	        items.removeIf(item -> item.getCno().equals(excludeCno)); // cno가 excludeCno와 일치하는 항목 제거
	    }

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

	// 상품 가격 변경-어드민 권한
	@Transactional
	public boolean updatePrice(ItemDto.price_update priceUpdateDto, String username) {
		Long itemNo = priceUpdateDto.getItemNo();
		Integer newPrice = priceUpdateDto.getItemPrice();

		// 1. 아이템 가격 업데이트
		Integer currentPrice = itemDao.findPriceByItemNo(itemNo);
		if (currentPrice == null) {
			return false;
		}
		itemDao.updatePrice(itemNo, newPrice);

		// 2. 장바구니에서 해당 상품의 가격을 업데이트
		cartDao.updateCartTotalPrice(username, itemNo); // 장바구니의 cart_totalprice 갱신

		return true;
	}
	
	// 상품 사이즈 목록 조회
    public List<String> getItemSizes(Long itemNo) {
        return itemDao.findSizesByItemNo(itemNo);
    }

    // 상품 사이즈별 재고 조회
    public Long getItemStock(Long itemNo, String itemSize) {
        return itemDao.findJangoByItemNoAndItemSize(itemNo, itemSize);
    }
	
	// 재고 업데이트
	@Transactional
    public boolean updateJango(ItemDto.jango_update jangoUpdateDto, String username) {
        Long itemNo = jangoUpdateDto.getItemNo();
        String itemSize = jangoUpdateDto.getItemSize();
        Long newJango = jangoUpdateDto.getItemJango();

        // 1. 현재 재고 값 조회
        Long currentJango = itemDao.findJangoByItemNoAndItemSize(itemNo, itemSize);
        if (currentJango == null) {
            return false; // 해당 사이즈에 대한 재고 정보가 없을 경우
        }

        // 2. 재고 값 업데이트
        itemDao.updateJango(itemNo, itemSize, newJango);
        
        // 3. 업데이트 성공
        return true;
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
