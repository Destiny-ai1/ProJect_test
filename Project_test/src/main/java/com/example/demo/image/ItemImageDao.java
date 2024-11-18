package com.example.demo.image;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;

@Mapper
public interface ItemImageDao {
    // 로그 객체 생성
    Log logger = LogFactory.getLog(ItemImageDao.class);

    // 이미지 저장 (imageNo는 자동 생성되므로 삽입하지 않음)
    @Insert("insert into item_image (item_no, image_name, image_no) VALUES (#{itemNo}, #{imageName}, ITEM_IMAGE_SEQ.NEXTVAL)")
    public Integer save(ItemImage itemImage);

    // 이미지 저장 (변경된 버전: 로그 추가 및 확인용 메서드)
    default Integer saveWithLogging(ItemImage itemImage) {
        logger.debug("Attempting to insert ItemImage: " + itemImage);
        
        // 실제 save 호출
        Integer result = save(itemImage);
        
        // 결과 확인
        if (result == 1) {
            logger.debug("Insert successful for ItemImage: " + itemImage);
        } else {
            logger.error("Failed to insert ItemImage: " + itemImage);
        }
        
        return result;
    }
}
