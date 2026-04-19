package com.yuqin.meinian.api.db.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseMapper;
import com.github.yulichang.toolkit.JoinWrappers;
import com.yuqin.meinian.api.db.entity.SysUserEntity;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;
import java.util.Set;

/**
 * @author YuQin
 * @description 针对表【sys_user(系统用户表)】的数据库操作Mapper
 * @createDate 2026-04-03 02:27:34
 * @Entity com.yuqin.meinian.api.db.entity.SysUserEntity
 */
public interface SysUserMapper extends MPJBaseMapper<SysUserEntity> {
    /**
     * 根据用户的ID查询该用户的权限
     *
     * @param userId
     * @return 权限列表
     */
    Set<String> selectPermissionsByUserId(int userId);

    /**
     * 根据用户的用户名和密码查询该用户ID
     *
     * @param username
     * @param password
     * @return userID
     */
    default Optional<Integer> selectIdByUserNameAndPassWordOpt(@Param("username") String username,
                                                            @Param("password") String password) {
        LambdaQueryWrapper<SysUserEntity> wrapper = Wrappers.lambdaQuery(SysUserEntity.class)
                .eq(SysUserEntity::getUsername, username)
                .eq(SysUserEntity::getPassword, password)
                .eq(SysUserEntity::getUserStatus, 1)
                .select(SysUserEntity::getUserId);

        Page<SysUserEntity> page = new Page<>(1, 1);
        IPage<SysUserEntity> result = this.selectPage(page, wrapper);

        return Optional.ofNullable(result.getRecords())
                .filter(records -> !records.isEmpty())
                .map(records -> records.getFirst().getUserId());
    }

//    /**
//     * 通过用户的ID返回用户的姓名
//     *
//     * @param userId 用户ID
//     * @return Optional<String> username
//     */
//    default Optional<String> selectUsernameById(@Param("userId") Integer userId) {
//        return Optional.ofNullable(this.selectOne(new LambdaQueryWrapper<SysUserEntity>()
//                        .select(SysUserEntity::getUsername)
//                        .eq(SysUserEntity::getUserId, userId)))
//                .map(SysUserEntity::getUsername);
//
//    }
    /**
     * 修改用户的密码
     *
     * @param newPassword 新密码
     * @param userId      用户Id
     * @return int
     */
    default int updatePasswordById(@Param("userId") Integer userId,
                                   @Param("newPassword") String newPassword) {
        LambdaUpdateWrapper<SysUserEntity> wrapper = Wrappers.lambdaUpdate(SysUserEntity.class)
                .eq(SysUserEntity::getUserId, userId)
                .set(SysUserEntity::getPassword, newPassword);
        return this.update( wrapper);
    }


//    default Optional<Integer> updateUserById(Integer userId, SysUserEntity user) {
//        if (userId == null || user == null) {
//            return Optional.empty();
//        }
//        // 使用官方的 LambdaUpdateWrapper
//        JoinWrappers.update(SysUserEntity.class)
//                .eq(SysUserEntity::getUserId, userId);
//
//        // 只更新 user 对象中非 null 的字段
//        int rows = this.update(user, wrapper);
//        return rows > 0 ? Optional.of(rows) : Optional.empty();
//    }
}




