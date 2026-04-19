package com.yuqin.meinian.api.serviceImpl;

import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.toolkit.JoinWrappers;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.yuqin.meinian.api.db.entity.SysDepartmentEntity;
import com.yuqin.meinian.api.db.mapper.SysDepartmentMapper;
import com.yuqin.meinian.api.mis.VO.DeptIdNameVO;
import com.yuqin.meinian.api.service.SysDepartmentService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author YuQin
 * @description 针对表【sys_department(部门信息表)】的数据库操作Service实现
 * @createDate 2026-04-03 02:27:34
 */
@Service
public class SysDepartmentServiceImpl extends MPJBaseServiceImpl<SysDepartmentMapper, SysDepartmentEntity>
        implements SysDepartmentService {

    @Override
    public List<DeptIdNameVO> queryDeptIdName() {
        MPJLambdaWrapper<SysDepartmentEntity> wrapper = JoinWrappers.lambda("dept", SysDepartmentEntity.class)
                .select(SysDepartmentEntity::getDeptName)
                .selectAs(SysDepartmentEntity::getId, DeptIdNameVO::getDeptId)
                .orderByAsc(SysDepartmentEntity::getId);
        return selectJoinList(DeptIdNameVO.class, wrapper);

    }
}