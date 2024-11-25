package com.example.demo.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ItemRestController {

    @Autowired
    private ItemService itemService;

    // 동적으로 상품 번호를 받아서 해당 상품 삭제
    @DeleteMapping("/item/delete/{itemNo}")
    public String deleteItem(@PathVariable Long itemNo) {
        boolean isDeleted = itemService.deleteItem(itemNo);

        if (isDeleted) {
            return "success";
        } else {
            return "fail";
        }
    }
}
