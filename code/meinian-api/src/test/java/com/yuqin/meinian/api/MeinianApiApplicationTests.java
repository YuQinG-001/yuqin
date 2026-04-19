package com.yuqin.meinian.api;

import com.github.yulichang.toolkit.JoinWrappers;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.yuqin.meinian.api.db.entity.SysUserEntity;
import com.yuqin.meinian.api.db.mapper.SysUserMapper;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MeinianApiApplicationTests {

    @Resource
    private SysUserMapper sysUserMapper;

    @Test
    void test001() {
        MPJLambdaWrapper<SysUserEntity> lambda = JoinWrappers.lambda(SysUserEntity.class);
        lambda.eq(SysUserEntity::getGender, "男");
        System.out.println(lambda.getCustomSqlSegment());
    }
}
