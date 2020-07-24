package com.leyou.order.dto;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class AddressDto {
    private Long id;
    private String name;
    private String phone;
    private String state;
    private String city;
    private String district;
    private String address;
    private String zipCode;
    private Boolean isDefault;
}
