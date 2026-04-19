package com.yuqin.meinian.api;

import com.yuqin.meinian.api.db.mapper.SysRoleMapper;
import com.yuqin.meinian.api.db.mapper.SysUserMapper;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

@SpringBootTest
class MeinianApiApplicationTests {

    @Resource
    private SysUserMapper sysUserMapper;
    @Test
    void test001() {
        Set<String> permissions = sysUserMapper.selectPermissionsByUserId(1);
        System.out.println(permissions);
        permissions.forEach(System.out::println);
    }

}
