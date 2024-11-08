package com.example.demo.address;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.exception.FailException;

@Service
public class AddressService {

    @Autowired
    private AddressDao addressDao;

    // 배송지 생성 로직
    @Transactional
    public Long createAddress(AddressDto.Create dto) {
        Address address = dto.toEntity();
        addressDao.save(address);
        return address.getAddressNo();
    }

    // 특정 배송지 조회 로직
    public AddressDto.Read getAddress(Long addressNo) {
        return addressDao.findById(addressNo)
                .orElseThrow(() -> new FailException("배송지를 찾을 수 없습니다"));
    }

    // 회원의 모든 배송지 조회 로직
    public List<AddressDto.Read> getAllAddresses(String memberUsername) {
        return addressDao.findAllByUsername(memberUsername);
    }

    // 배송지 업데이트 로직
    @Transactional
    public void updateAddress(AddressDto.Update dto) {
        addressDao.update(dto.toEntity());
    }

    // 배송지 삭제 로직
    @Transactional
    public void deleteAddress(Long addressNo) {
        addressDao.delete(addressNo);
    }
}
