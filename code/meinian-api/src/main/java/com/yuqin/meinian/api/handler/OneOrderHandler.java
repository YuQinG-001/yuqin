package com.yuqin.meinian.api.handler;

import com.yuqin.meinian.api.mis.DTO.QueryUserPageDTO;
import lombok.Data;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.function.Function;

//@Component
//@Order(1)
public class OneOrderHandler implements OrderHandler {
    @Override
    public String handler(QueryUserPageDTO dto, Function<QueryUserPageDTO, String> next) {
        System.out.println("OneOrderHandler执行了");
        return next.apply(dto);
    }
}
