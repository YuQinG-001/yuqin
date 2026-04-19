package com.yuqin.meinian.api.handler;

import com.yuqin.meinian.api.mis.DTO.QueryUserPageDTO;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;
//@Service
public class OrderService {
    List<OrderHandler> orderHandlers;

    public String validOrder(QueryUserPageDTO dto) {
        orderHandlers.sort(AnnotationAwareOrderComparator.INSTANCE);
        Function<QueryUserPageDTO, String> chain = buildOrderChain();
        return chain.apply(dto);
    }

 /*   private Function<QueryUserPageDTO, String> buildOrderChain() {
        Function<QueryUserPageDTO, String> chain = (dto) -> "成功";
        for (OrderHandler orderHandler : orderHandlers) {
            final OrderHandler curHandler = orderHandler;
            final Function<QueryUserPageDTO, String> next = chain;
            chain = dto -> curHandler.handler(dto, next);
        }
        return chain;
    }*/

    private Function<QueryUserPageDTO, String> buildOrderChain() {
        // 初始值：链尾
        Function<QueryUserPageDTO, String> initialChain = dto -> "成功";

        // 使用 reduce 从左到右累积
        return orderHandlers.stream()
                .reduce(initialChain,
                        (chain, handler) -> dto -> handler.handler(dto, chain),
                        (c1, c2) -> {
                            throw new UnsupportedOperationException("并行流不支持");
                        });
    }
}
