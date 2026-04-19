package com.yuqin.meinian.api.serviceImpl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.MD5;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.toolkit.JoinWrappers;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.yuqin.meinian.api.db.entity.SysDepartmentEntity;
import com.yuqin.meinian.api.db.entity.SysRoleEntity;
import com.yuqin.meinian.api.db.entity.SysUserEntity;
import com.yuqin.meinian.api.db.mapper.SysUserMapper;
import com.yuqin.meinian.api.exception.HisException;
import com.yuqin.meinian.api.mis.DTO.LoginDTO;
import com.yuqin.meinian.api.mis.DTO.QueryUserPageDTO;
import com.yuqin.meinian.api.mis.DTO.UpdatePasswordDTO;
import com.yuqin.meinian.api.mis.DTO.UpdateUserDIO;
import com.yuqin.meinian.api.mis.VO.UserPageVO;
import com.yuqin.meinian.api.service.SysUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * @author YuQin
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class SysUserServiceImpl extends MPJBaseServiceImpl<SysUserMapper, SysUserEntity> implements SysUserService {


    private static final int SALT_PREFIX_LEN = 6;
    private static final int SALT_SUFFIX_LEN = 3;
    private static final MD5 MD5_INSTANCE = MD5.create();

    @Override
    public Integer login(LoginDTO loginDTO) {
        String username = loginDTO.getUsername();
        String password = loginDTO.getPassword();

        // 基础参数校验：用户名或密码为空，直接抛业务异常
        if (StrUtil.isBlank(username) || StrUtil.isBlank(password)) {
            throw new HisException("用户名或密码不能为空");
        }

        String encryptedPassword = buildEncryptedPassword(username, password);

        // 复用 Mapper 方法，返回 null 表示账号或密码错误
        return baseMapper.selectIdByUserNameAndPassWordOpt(username, encryptedPassword)
                .orElseThrow(() -> new HisException(500 ,"用户名或密码错误"));
    }

    /**
     * 根据用户名和密码生成最终存储/比对用的密码串
     * 规则：用户名MD5前6位 + 密码MD5 + 用户名MD5后3位
     */
    private String buildEncryptedPassword(String username, String password) {
        String usernameMd5 = MD5_INSTANCE.digestHex(username);
        String prefix = StrUtil.subWithLength(usernameMd5, 0, SALT_PREFIX_LEN);
        String suffix = StrUtil.subSufByLength(usernameMd5, SALT_SUFFIX_LEN);

        String passwordMd5 = MD5_INSTANCE.digestHex(password);

        return prefix + passwordMd5 + suffix;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean modifyPassword(UpdatePasswordDTO updatePasswordDTO) {
        String oldPassword = updatePasswordDTO.getOldPassword();
        String newPassword = updatePasswordDTO.getNewPassword();

        // 1. 基础非空校验
        if (StrUtil.isBlank(oldPassword) || StrUtil.isBlank(newPassword)) {
            throw new HisException("旧密码或新密码不能为空");
        }

        // 2. 新旧密码不能相同
        if (oldPassword.equals(newPassword)) {
            throw new HisException("新密码不能与旧密码相同");
        }

        Integer userId = StpUtil.getLoginIdAsInt();

        // 3. 查询完整用户信息（需要拿到加密后的密码用于比对）
        SysUserEntity user = Optional.ofNullable(baseMapper.selectById(userId))
                .orElseThrow(() -> new HisException("用户不存在"));

        String username = user.getUsername();

        // 4. 加密旧密码，与数据库中的密码比对
        String encryptedOldPassword = buildEncryptedPassword(username, oldPassword);
        if (!user.getPassword().equals(encryptedOldPassword)) {
            throw new HisException("原密码错误");
        }

        // 5. 加密新密码
        String encryptedNewPassword = buildEncryptedPassword(username, newPassword);

        // 6. 执行更新
        int rows = baseMapper.updatePasswordById(userId, encryptedNewPassword);
        if (rows != 1) {
            throw new HisException("密码修改失败，请稍后重试");
        }

        return true;
    }


    @Override
    public IPage<UserPageVO> queryUserWithRolesPage(QueryUserPageDTO dto) {
        // 分页参数友好处理
        if (dto.getPageNum() == null || dto.getPageNum() < 1) {
            dto.setPageNum(1);
        }
        if (dto.getPageSize() == null || dto.getPageSize() < 1) {
            dto.setPageSize(10);
        }

        MPJLambdaWrapper<SysUserEntity> wrapper = buildUserWithRolesPageWrapper(dto);
        Page<UserPageVO> page = Page.of(dto.getPageNum(), dto.getPageSize());

        return selectJoinListPage(page, UserPageVO.class, wrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveUser(SysUserEntity sysUserEntity) {
        // 1. 非空校验（可选，前端通常会做，但后端必须兜底）
        String username = sysUserEntity.getUsername();
        if (username == null || username.trim().isEmpty()) {
            throw new HisException("用户名不能为空");
        }

        // 2. 用户名唯一性校验
        MPJLambdaWrapper<SysUserEntity> queryWrapper = JoinWrappers.lambda(SysUserEntity.class);
        queryWrapper.eq(SysUserEntity::getUsername, username);
        Long count = baseMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new HisException("用户名已存在，请重新输入");
        }

        // 3. 加密密码并保存
        String password = buildEncryptedPassword(sysUserEntity.getUsername(), sysUserEntity.getPassword());
        sysUserEntity.setPassword(password);
        return SqlHelper.retBool(baseMapper.insert(sysUserEntity));
    }

    @Override
    public SysUserEntity queryUser(int userId) {
        MPJLambdaWrapper<SysUserEntity> wrapper = JoinWrappers.lambda(SysUserEntity.class)
                .select(SysUserEntity::getUsername, SysUserEntity::getRealName, SysUserEntity::getGender,
                        SysUserEntity::getMobile, SysUserEntity::getEmail, SysUserEntity::getHireDate,
                        SysUserEntity::getRoleIds, SysUserEntity::getDeptId)
                .eq(SysUserEntity::getUserId, userId);
        try {
            return getOneOpt(wrapper, true)
                    .orElseThrow(() -> new HisException("无法通过ID查询到用户信息"));
        } catch (TooManyResultsException e) {
            log.error("数据完整性错误：用户ID {} 查询到多条记录！", userId, e);
            throw new HisException("数据异常，请联系管理员");
        }

    }

    private MPJLambdaWrapper<SysUserEntity> buildUserWithRolesPageWrapper(QueryUserPageDTO dto) {
        MPJLambdaWrapper<SysUserEntity> wrapper = JoinWrappers.lambda("user", SysUserEntity.class)
                .select(SysUserEntity::getUserId,
                        SysUserEntity::getRealName,
                        SysUserEntity::getGender,
                        SysUserEntity::getMobile,
                        SysUserEntity::getHireDate,
                        SysUserEntity::getEmail,
                        SysUserEntity::getUserStatus,
                        SysUserEntity::getIsSuperAdmin)
                .select(SysDepartmentEntity::getDeptName)
                .select("GROUP_CONCAT(role.role_name SEPARATOR ',') AS roles")
                //关联条件
                .leftJoin(SysDepartmentEntity.class, "dept", SysDepartmentEntity::getId, SysUserEntity::getDeptId)
                .innerJoin(SysRoleEntity.class, "role",
                        on -> on.apply("JSON_CONTAINS(user.role_ids, CAST(role.role_id AS JSON))"))
                // 动态条件
                .like(StrUtil.isNotBlank(dto.getRealName()), SysUserEntity::getRealName, dto.getRealName())
                .eq(StrUtil.isNotBlank(dto.getGender()), SysUserEntity::getGender, dto.getGender())
                .eq(ObjectUtil.isNotNull(dto.getDeptId()), SysDepartmentEntity::getId, dto.getDeptId())
//                .eq(ObjectUtil.isNotNull(dto.getRoleId()), SysRoleEntity::getRoleId, dto.getRoleId())
                .eq(ObjectUtil.isNotNull(dto.getUserStatus()), SysUserEntity::getUserStatus, dto.getUserStatus())
                .groupBy(SysUserEntity::getUserId,
                        SysUserEntity::getRealName,
                        SysUserEntity::getGender,
                        SysUserEntity::getMobile,
                        SysUserEntity::getHireDate)
                .groupBy(SysDepartmentEntity::getDeptName);
        if (ObjectUtil.isNotNull(dto.getRoleId())) {
            wrapper.exists("SELECT 1 FROM sys_role r WHERE JSON_CONTAINS(user.role_ids, CAST(r.role_id AS JSON)) AND r.role_id = {0}", dto.getRoleId());
        }
        wrapper.orderByDesc(SysUserEntity::getUserId);
        return wrapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)  // 确保事务
    public Boolean modifyUser(UpdateUserDIO dto) {
        // 使用标准 LambdaUpdateWrapper（单表更新首选）
        LambdaUpdateWrapper<SysUserEntity> wrapper = Wrappers.lambdaUpdate(SysUserEntity.class)
                .eq(SysUserEntity::getUserId, dto.getUserId());

        // 条件性设置字段，避免 null 覆盖
        wrapper.set(StrUtil.isNotBlank(dto.getUsername()), SysUserEntity::getUsername, dto.getUsername())
                .set(StrUtil.isNotBlank(dto.getRealName()), SysUserEntity::getRealName, dto.getRealName())
                .set(StrUtil.isNotBlank(dto.getGender()), SysUserEntity::getGender, dto.getGender())
                .set(StrUtil.isNotBlank(dto.getMobile()), SysUserEntity::getMobile, dto.getMobile())
                .set(StrUtil.isNotBlank(dto.getEmail()), SysUserEntity::getEmail, dto.getEmail())
                .set(dto.getHireDate() != null, SysUserEntity::getHireDate, dto.getHireDate())
                .set(dto.getDeptId() != null, SysUserEntity::getDeptId, dto.getDeptId());
               // .set(dto.getRoleIds() != null, SysUserEntity::getRoleIds, dto.getRoleIds());
        if (dto.getRoleIds() != null) {
            String jsonStr = JSONUtil.toJsonStr(dto.getRoleIds());
            wrapper.set(SysUserEntity::getRoleIds, jsonStr);
        }
        int rows = baseMapper.update(wrapper);  // 使用标准 update 方法

        if (rows != 1) {
            throw new HisException("用户不存在或数据未变更，userId: " + dto.getUserId());
        }
        StpUtil.logout(dto.getUserId());
        return true;
    }

}