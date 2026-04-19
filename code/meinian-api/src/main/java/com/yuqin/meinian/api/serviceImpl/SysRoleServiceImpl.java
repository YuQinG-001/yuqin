package com.yuqin.meinian.api.serviceImpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.toolkit.JoinWrappers;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.yuqin.meinian.api.db.entity.SysRoleEntity;
import com.yuqin.meinian.api.mis.VO.RoleNameListVO;
import com.yuqin.meinian.api.service.SysRoleService;
import com.yuqin.meinian.api.db.mapper.SysRoleMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author YuQin
* @description 针对表【sys_role(系统角色表)】的数据库操作Service实现
* @createDate 2026-04-03 02:27:34
*/
@Service
public class SysRoleServiceImpl extends MPJBaseServiceImpl<SysRoleMapper, SysRoleEntity>
    implements SysRoleService{
    @Override
    public List<RoleNameListVO> queryRoleNameList() {
        MPJLambdaWrapper<SysRoleEntity> role = JoinWrappers.lambda("role", SysRoleEntity.class)
                .select(SysRoleEntity::getRoleName)
                .select(SysRoleEntity::getRoleId)
                .orderByAsc(SysRoleEntity::getRoleId);
        return selectJoinList(RoleNameListVO.class, role);
    }
}




