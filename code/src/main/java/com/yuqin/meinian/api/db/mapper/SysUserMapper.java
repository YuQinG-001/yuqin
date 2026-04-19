package com.yuqin.meinian.api.db.mapper;

import com.yuqin.meinian.api.db.entity.SysUserEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.Set;

/**
* @author YuQin
* @description 针对表【sys_user(系统用户表)】的数据库操作Mapper
* @createDate 2026-04-02 20:29:13
* @Entity com.yuqin.meinian.api.db.entity.SysUserEntity
*/
public interface SysUserMapper extends BaseMapper<SysUserEntity> {
    /**
     * 根据用户的ID查询该用户的权限
     * @param userId
     * @return 权限列表
     */
    Set<String> selectPermissionsByUserId(int userId);
}




