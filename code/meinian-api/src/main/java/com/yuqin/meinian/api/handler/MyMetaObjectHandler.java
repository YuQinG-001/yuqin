package com.yuqin.meinian.api.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        // 插入时自动填充 createDate 为当前日期
        this.strictInsertFill(metaObject, "createDate", LocalDate.class, LocalDate.now());
        // 也可以填充其他字段，如 updateTime 等
    }

    @Override
    public void updateFill(MetaObject metaObject) {

    }
}
