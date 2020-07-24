package com.leyou.order.client;

import com.leyou.order.dto.AddressDto;

import java.util.ArrayList;
import java.util.List;

public abstract class AddressClient {
    public static final List<AddressDto> addressList = new ArrayList<AddressDto>(){{
        AddressDto address = new AddressDto();
        address.setId(1L);
        address.setAddress("航头镇航头路18号传智播客 3号楼");
        address.setCity("上海");
        address.setDistrict("浦东新区");
        address.setName("虎哥");
        address.setPhone("15800000000");
        address.setState("上海");
        address.setZipCode("210000");
        address.setIsDefault(true);
        address.setName("虎哥");
        add(address);
        AddressDto address2 = new AddressDto();
        address2.setId(2L);
        address2.setAddress("天堂路 3号路");
        address2.setCity("北京");
        address2.setDistrict("朝阳区");
        address2.setName("张三");
        address2.setPhone("13600000000");
        address2.setState("北京");
        address2.setZipCode("100000");
        address2.setIsDefault(false);
        address2.setName("张三");
        add(address2);
        }
    };
    public static AddressDto findById(Long id){
        for (AddressDto addressDto : addressList) {
            if(addressDto.getId() == id) return addressDto;
        }
        return null;
    }
}
