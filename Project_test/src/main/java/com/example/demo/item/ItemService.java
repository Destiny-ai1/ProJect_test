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

@Service
public class ItemService {
    @Autowired
    private ItemDao itemDao;
    @Autowired
    private CategoryDao categoryDao;
    @Autowired
    private ItemImageDao imageDao;

    public List<Map> findMajorCategory() {
        return categoryDao.findMajorCategory();
    }

    public List<ItemDto.ItemList> findAll(String imageUrl) {
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            imageUrl = "/default/images/";  // 기본 이미지 URL을 설정 (예시)
        }
        return itemDao.findAll(imageUrl);
    }

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

 // 상품 상세 조회
    public ItemDto.Read read(Long itemNo, String imageUrl) {
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            imageUrl = "/api/images?imagename=";  // 기본 이미지 URL
        }

        // DB에서 상품 정보를 조회하며, 이미지 URL도 함께 가져옵니다.
        ItemDto.Read itemDto = itemDao.findById(itemNo, imageUrl);

        // 만약 아이템에 이미지가 없다면 기본 이미지 경로 설정
        if (itemDto != null && (itemDto.getItemImages() == null || itemDto.getItemImages().isEmpty())) {
            itemDto.setItemImages(List.of("normal/default-image.jpg"));
        }

        return itemDto;
    }
}

