package com.yuqin.meinian.api.service;

import com.github.yulichang.base.MPJBaseService;
import com.yuqin.meinian.api.db.entity.SysDepartmentEntity;
import com.yuqin.meinian.api.mis.VO.DeptIdNameVO;

import java.util.List;

/**
 * @author YuQin
 * @description 针对表【sys_department(部门信息表)】的数据库操作Service
 * @createDate 2026-04-03 02:27:34
 */
public interface SysDepartmentService extends MPJBaseService<SysDepartmentEntity> {
    /**
     * 查询部门ID与名称列表
     * <p>
     * 可用于前端下拉框、部门选项等场景。
     * </p>
     *
     * @return 部门ID与名称的VO列表，若无匹配数据则返回空列表
     */
    List<DeptIdNameVO> queryDeptIdName();
}
