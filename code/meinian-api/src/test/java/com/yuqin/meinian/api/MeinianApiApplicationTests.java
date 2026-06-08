package com.yuqin.meinian.api;

import cn.hutool.json.JSONUtil;
import com.github.yulichang.toolkit.JoinWrappers;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.yuqin.meinian.api.db.entity.SysUserEntity;
import com.yuqin.meinian.api.db.mapper.MedExamPackageMapper;
import com.yuqin.meinian.api.db.mapper.SysUserMapper;
import com.yuqin.meinian.api.mis.VO.ExamPackageDetailVO;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.LocalTime;

import static java.time.LocalTime.now;

@SpringBootTest
class MeinianApiApplicationTests {

    @Resource
    private SysUserMapper        sysUserMapper;
    @Autowired
    private MedExamPackageMapper medExamPackageMapper;

    @Test
    void test001() {

        System.out.println("-------------------"+ LocalDateTime.now().toLocalDate());
    }
}
