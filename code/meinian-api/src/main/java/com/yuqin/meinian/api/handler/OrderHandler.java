package com.yuqin.meinian.api.handler;

import com.yuqin.meinian.api.mis.DTO.QueryUserPageDTO;

import java.util.function.Function;

public interface OrderHandler {
    String handler(QueryUserPageDTO dto, Function<QueryUserPageDTO,String> next);
}
