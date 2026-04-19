package com.yuqin.meinian.api.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import com.yuqin.meinian.api.db.entity.SysUserEntity;
import com.yuqin.meinian.api.exception.HisException;
import com.yuqin.meinian.api.mis.DTO.LoginDTO;
import com.yuqin.meinian.api.mis.DTO.QueryUserPageDTO;
import com.yuqin.meinian.api.mis.DTO.UpdatePasswordDTO;
import com.yuqin.meinian.api.mis.DTO.UpdateUserDIO;
import com.yuqin.meinian.api.mis.VO.UserPageVO;

/**
 * @author YuQin
 * @description 针对表【sys_user(系统用户表)】的数据库操作Service
 * @createDate 2026-04-03 02:27:34
 */
public interface SysUserService extends MPJBaseService<SysUserEntity> {
    /**
     * 用户登录业务
     * <p>
     * 校验账号密码，处理登录逻辑，并返回用户标识或 Token
     * </p>
     *
     * @param loginDTO 登录请求参数（包含账号、密码、验证码等）
     * @return 登录成功后返回用户 ID 或 Token 标识（具体依业务而定，通常返回用户ID）
     */
    Integer login(LoginDTO loginDTO);

    /**
     * 修改用户密码
     * <p>
     * 通常需要校验旧密码是否正确，以及新密码是否符合复杂度要求
     * </p>
     *
     * @param updatePasswordDTO 密码修改参数（包含用户ID、旧密码、新密码）
     * @return true-修改成功；false-修改失败（具体可抛异常或返回布尔值）
     */
    boolean modifyPassword(UpdatePasswordDTO updatePasswordDTO);

    /**
     * 分页查询用户信息及其关联的角色信息
     * <p>
     * 支持按真实姓名、性别、部门ID、角色ID、用户状态等条件筛选，
     * 返回结果中 roles 字段为逗号分隔的角色名称字符串
     * </p>
     *
     * @param queryUserPageDTO 分页查询条件（包含页码、每页条数及各种筛选字段）
     * @return MyBatis-Plus 分页对象，内含 UserPageVO 列表及总记录数等分页信息
     */
    IPage<UserPageVO> queryUserWithRolesPage(QueryUserPageDTO queryUserPageDTO);

    /**
     * 保存用户信息（含角色关联）
     * <p>
     * 该方法会先保存用户主表信息，若主表保存成功，
     * 再根据用户拥有的角色ID列表，批量插入用户-角色关系表。
     * 整个操作在一个数据库事务中执行，保证数据一致性。
     * </p>
     *
     * @param sysUserEntity 用户实体对象，必须包含基本字段（如姓名、性别等）。
     *                      若实体中包含了角色ID列表字段，需确保其格式正确。
     * @return {@code true} 表示用户及角色关联全部保存成功；
     * {@code false} 表示保存过程中发生异常，事务已回滚。
     * @throws RuntimeException 当主表插入失败、角色列表为空或关系表插入异常时抛出，
     *                          触发事务回滚。具体异常信息可通过日志查看。
     */
    boolean saveUser(SysUserEntity sysUserEntity);

    /**
     * 根据用户ID查询用户基本信息
     * <p>
     * 该方法会从数据库查询指定用户的非敏感信息字段，包括用户名、真实姓名、性别、
     * 手机号、邮箱、入职日期、角色ID列表及部门ID。
     * 若用户不存在，将抛出 {@link HisException} 业务异常；若因数据问题导致
     * 查询结果不唯一，将抛出 {@link HisException} 提示系统异常。
     * </p>
     *
     * @param userId 用户ID，主键唯一标识
     * @return 包含用户信息的实体对象，永远不会返回 {@code null}
     * @throws HisException 当用户不存在或数据完整性异常时抛出
     */
    SysUserEntity queryUser(int userId);

    /**
     * 用户信息更新 DTO（Data Transfer Object）
     * <p>
     * 用于 MIS 后台管理系统中修改用户基础信息时接收前端提交的数据。
     * 包含用户名、姓名、性别、手机号、邮箱、部门、入职日期及角色列表等字段，
     * 所有字段均附带 JSR-303 校验注解，确保数据格式符合业务要求。
     * </p>
     *
     * @author your_name
     * @since 2026-04-17
     */
    Boolean modifyUser(UpdateUserDIO dto);
}