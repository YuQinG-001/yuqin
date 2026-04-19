package com.yuqin.meinian.api.service;

import com.github.yulichang.base.MPJBaseService;
import com.yuqin.meinian.api.db.entity.SysRoleEntity;
import com.yuqin.meinian.api.mis.VO.RoleNameListVO;

import java.util.List;

/**
 * @author YuQin
 * @description 针对表【sys_role(系统角色表)】的数据库操作Service
 * @createDate 2026-04-03 02:27:34
 */
public interface SysRoleService extends MPJBaseService<SysRoleEntity> {
    /**
     * 查询所有角色的ID和名称列表
     * <p>
     * 仅返回 roleId 和 roleName 两个字段，按角色ID升序排列。
     * 常用于前端下拉框、选项列表等场景。
     * </p>
     *
     * @return 角色ID与名称的VO列表，若无数据则返回空列表
     */
    List<RoleNameListVO> queryRoleNameList();
}
