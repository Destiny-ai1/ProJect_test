package com.example.demo.address;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.validation.Valid;

@Controller
public class AddressController {

    @Autowired
    private AddressService addressService;

    // 배송지 생성 폼을 보여주는 메소드
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/address/create")
    public ModelAndView createAddressForm() {
        return new ModelAndView("address/create");
    }

    // 배송지 생성 요청을 처리하는 메소드
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/address/create")
    public ModelAndView createAddress(@Valid AddressDto.Create dto, BindingResult br, Principal principal) {
        if (br.hasErrors()) {
            return new ModelAndView("address/create").addObject("errors", br.getAllErrors());
        }
        addressService.createAddress(dto);
        return new ModelAndView("redirect:/address/list");
    }

    // 특정 배송지 조회 메소드
    @GetMapping("/address/read")
    public ModelAndView readAddress(@RequestParam Long addressNo) {
        AddressDto.Read dto = addressService.getAddress(addressNo);
        return new ModelAndView("address/read").addObject("result", dto);
    }

    // 회원의 모든 배송지 조회 메소드
    @GetMapping("/address/list")
    public ModelAndView listAddresses(Principal principal) {
        String memberUsername = principal.getName();
        return new ModelAndView("address/list").addObject("result", addressService.getAllAddresses(memberUsername));
    }

    // 배송지 수정 폼을 보여주는 메소드
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/address/update")
    public ModelAndView updateAddressForm(Long addressNo) {
        AddressDto.Read dto = addressService.getAddress(addressNo);
        return new ModelAndView("address/update").addObject("result", dto);
    }

    // 배송지 수정 요청을 처리하는 메소드
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/address/update")
    public ModelAndView updateAddress(@Valid AddressDto.Update dto, BindingResult br) {
        if (br.hasErrors()) {
            return new ModelAndView("address/update").addObject("errors", br.getAllErrors());
        }
        addressService.updateAddress(dto);
        return new ModelAndView("redirect:/address/list");
    }

    // 배송지 삭제 요청 처리 메소드
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/address/delete")
    public ModelAndView deleteAddress(@RequestParam Long addressNo) {
        addressService.deleteAddress(addressNo);
        return new ModelAndView("redirect:/address/list");
    }
}
