package com.yuqin.meinian.api.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import com.yuqin.meinian.api.db.entity.SysUserEntity;
import com.yuqin.meinian.api.mis.DTO.*;
import com.yuqin.meinian.api.mis.VO.UserPageVO;

import java.util.List;

/**
 * @author YuQin
 * @description 针对表【sys_user(系统用户表)】的数据库操作Service
 * @createDate 2026-04-03 02:27:34
 */
public interface SysUserService extends MPJBaseService<SysUserEntity> {

    Integer login(LoginDTO loginDTO);


    boolean modifyPassword(UpdatePasswordDTO updatePasswordDTO);


    IPage<UserPageVO> queryUserWithRolesPage(QueryUserPageDTO queryUserPageDTO);


    boolean saveUser(SysUserEntity sysUserEntity);


    SysUserEntity queryUser(int userId);


    int modifyUser(UpdateUserDTO dto);


    int removeUserByIds(List<Integer> ids);

    /**
     * 用户离职时，将用户的userStatus修改为2:离职
     * 管理员不可离职，离职人员不可离职
     * @return
     */
    int dismissUser(List<Integer> ids);
}