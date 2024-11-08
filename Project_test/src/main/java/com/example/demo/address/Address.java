package com.example.demo.address;

import org.springframework.stereotype.Component;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Component
@Builder
public class Address {
	private Long addressNo;
    private String addressMain;
    private int postNo;
    private String addressRoad;
    private String addressDetail;
    private String addressName;
    
    private String memberUsername;
}
