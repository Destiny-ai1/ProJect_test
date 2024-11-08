package com.example.demo.address;

import jakarta.validation.constraints.*;
import lombok.*;

public class AddressDto {
    private AddressDto() {}

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Create {
        @NotNull
        private String addressMain;
        @NotNull
        private int postNo;
        @NotEmpty
        private String addressRoad;
        @NotEmpty
        private String addressDetail;
        @NotEmpty
        private String addressName;
        @NotEmpty
        private String memberUsername;

        public Address toEntity() {
            return new Address(null, addressMain, postNo, addressRoad, addressDetail, addressName, memberUsername);
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Read {
        private Long addressNo;
        private String addressMain;
        private int postNo;
        private String addressRoad;
        private String addressDetail;
        private String addressName;
        private String memberUsername;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Update {
        @NotNull
        private Long addressNo;
        @NotEmpty
        private String addressDetail;
        @NotEmpty
        private String addressName;

        public Address toEntity() {
            return Address.builder().addressNo(addressNo).addressDetail(addressDetail).addressName(addressName).build();
        }
    }
}
