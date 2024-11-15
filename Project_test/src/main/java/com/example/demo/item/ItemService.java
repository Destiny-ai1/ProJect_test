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
        return itemDao.findAll(ItemImageSaveLoad.IMAGE_URL);
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

        // 2. itemNo가 부모 테이블에 존재하는지 확인
        if (itemDao.getItemNameById(itemNo) == null) {
            throw new RuntimeException("상품 정보가 제대로 저장되지 않았습니다. itemNo: " + itemNo);
        }

        List<MultipartFile> images = dto.getImages();  // 이미지 목록

        // 이미지가 없으면 빈 리스트를 사용
        if (images == null || images.isEmpty()) {
            images = new ArrayList<>();
        }

        // 3. 상품에 대한 이미지 저장
        for (int i = 0; i < images.size(); i++) {
            MultipartFile imageFile = images.get(i);
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

                // 이미지 정보를 DB에 저장
                imageDao.save(new ItemImage(itemNo, i, saveFilename));  // itemNo를 사용하여 이미지 저장
                System.out.println("이미지 DB 저장: " + saveFilename);  // DB 저장 로그 출력

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
