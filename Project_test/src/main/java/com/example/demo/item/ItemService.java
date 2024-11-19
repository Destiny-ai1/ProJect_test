package com.example.demo.item;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
import com.example.demo.item.ItemDto.ItemList;

@Service
public class ItemService {
    @Autowired
    private ItemDao itemDao;
    @Autowired
    private CategoryDao categoryDao;
    @Autowired
    private ItemImageDao imageDao;

    // 카테고리 대분류
    public List<Map> findMajorCategory() {
        return categoryDao.findMajorCategory();
    }
    // 아이템 리스트
    public List<ItemDto.ItemList> findAll(String imageUrl) {
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            // imageUrl이 null이거나 빈 문자열이면 기본 URL을 설정
            imageUrl = "/default/images/";  // 기본 이미지 URL을 설정 (예시)
        }
        // 이제 imageUrl을 사용해서 Dao를 호출
        return itemDao.findAll(imageUrl);
    }


    // 상품 생성 서비스 (트랜잭션 처리 추가)
    @Transactional
    public void save(ItemDto.Create dto) {
        // 1. 상품 정보를 DB에 저장
        Item item = dto.toEntity();  // ItemDto.Create를 Item으로 변환
        itemDao.save(item);          // item을 DB에 저장
        
        // itemNo를 가져옵니다. (아이템이 저장된 후 itemNo가 생성되어야 합니다)
        Long itemNo = item.getItemNo();
        if (itemNo == null) {
            throw new RuntimeException("상품 저장 실패: itemNo가 생성되지 않았습니다.");
        }

        // 2. 상품에 대한 이미지 저장
        List<MultipartFile> images = dto.getImages();  // 이미지 목록
        if (images == null || images.isEmpty()) {
            images = new ArrayList<>();
        }

        // 3. 이미지를 `item_image` 테이블에 저장
        for (long i = 0; i < images.size(); i++) {
            MultipartFile imageFile = images.get((int)i);
            if (imageFile.isEmpty()) {
                continue;  // 이미지 파일이 비어있으면 건너뛰기
            }

            // 파일 확장자 추출
            String extension = FilenameUtils.getExtension(imageFile.getOriginalFilename());
            String saveFilename = UUID.randomUUID().toString() + "." + extension;

            // 저장할 파일 경로 지정
            File file = new File(ItemImageSaveLoad.IMAGE_FOLDER, saveFilename);
            try {
                // 이미지 파일을 실제 디스크에 저장
                imageFile.transferTo(file);
                
                // item_no를 사용하여 item_image 테이블에 이미지 저장
                ItemImage itemImage = new ItemImage(null, itemNo, saveFilename); // imageNo는 null로 설정 (자동 생성됨)
                imageDao.save(itemImage);  // item_image에 이미지 저장
            } catch (IOException e) {
                // 예외 처리: 파일 저장에 실패한 경우
                e.printStackTrace();
                throw new RuntimeException("이미지 저장 중 오류 발생", e);  // 예외를 던져 트랜잭션 롤백
            }
        }
    }

    // 상품 번호를 이용하여 상품 이름을 조회
    public String getItemNameById(Long itemNo) {
        return itemDao.getItemNameById(itemNo);
    }

    // 상품 번호로 상품 읽기
    public ItemDto.Read read(Long itemNo) {
        return itemDao.findById(itemNo, ItemImageSaveLoad.IMAGE_URL);
    }
}
