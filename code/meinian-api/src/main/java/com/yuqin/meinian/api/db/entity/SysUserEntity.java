package com.yuqin.meinian.api.db.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 系统用户表
 * @TableName sys_user
 */
@TableName(value ="sys_user")
@Data
public class SysUserEntity implements Serializable {
    /**
     * 用户ID
     */
    @TableId(type = IdType.AUTO)
    private Integer userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 性别
     */
    private String gender;

    /**
     * 手机号码
     */
    private String mobile;

    /**
     * 电子邮箱
     */
    private String email;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 开放平台ID
     */
    private String openId;

    /**
     * 部门ID
     */
    private Integer deptId;

    /**
     * 入职日期
     */
    private LocalDate hireDate;

    /**
     * 角色ID集合
     */
    private String roleIds;

    /**
     * 是否超级管理员(0:否,1:是)
     */
    private Integer isSuperAdmin;

    /**
     * 用户状态(1:在职,2:离职)
     */
    private Integer userStatus;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        SysUserEntity other = (SysUserEntity) that;
        return (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getUsername() == null ? other.getUsername() == null : this.getUsername().equals(other.getUsername()))
            && (this.getPassword() == null ? other.getPassword() == null : this.getPassword().equals(other.getPassword()))
            && (this.getRealName() == null ? other.getRealName() == null : this.getRealName().equals(other.getRealName()))
            && (this.getGender() == null ? other.getGender() == null : this.getGender().equals(other.getGender()))
            && (this.getMobile() == null ? other.getMobile() == null : this.getMobile().equals(other.getMobile()))
            && (this.getEmail() == null ? other.getEmail() == null : this.getEmail().equals(other.getEmail()))
            && (this.getAvatar() == null ? other.getAvatar() == null : this.getAvatar().equals(other.getAvatar()))
            && (this.getOpenId() == null ? other.getOpenId() == null : this.getOpenId().equals(other.getOpenId()))
            && (this.getDeptId() == null ? other.getDeptId() == null : this.getDeptId().equals(other.getDeptId()))
            && (this.getHireDate() == null ? other.getHireDate() == null : this.getHireDate().equals(other.getHireDate()))
            && (this.getRoleIds() == null ? other.getRoleIds() == null : this.getRoleIds().equals(other.getRoleIds()))
            && (this.getIsSuperAdmin() == null ? other.getIsSuperAdmin() == null : this.getIsSuperAdmin().equals(other.getIsSuperAdmin()))
            && (this.getUserStatus() == null ? other.getUserStatus() == null : this.getUserStatus().equals(other.getUserStatus()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getUsername() == null) ? 0 : getUsername().hashCode());
        result = prime * result + ((getPassword() == null) ? 0 : getPassword().hashCode());
        result = prime * result + ((getRealName() == null) ? 0 : getRealName().hashCode());
        result = prime * result + ((getGender() == null) ? 0 : getGender().hashCode());
        result = prime * result + ((getMobile() == null) ? 0 : getMobile().hashCode());
        result = prime * result + ((getEmail() == null) ? 0 : getEmail().hashCode());
        result = prime * result + ((getAvatar() == null) ? 0 : getAvatar().hashCode());
        result = prime * result + ((getOpenId() == null) ? 0 : getOpenId().hashCode());
        result = prime * result + ((getDeptId() == null) ? 0 : getDeptId().hashCode());
        result = prime * result + ((getHireDate() == null) ? 0 : getHireDate().hashCode());
        result = prime * result + ((getRoleIds() == null) ? 0 : getRoleIds().hashCode());
        result = prime * result + ((getIsSuperAdmin() == null) ? 0 : getIsSuperAdmin().hashCode());
        result = prime * result + ((getUserStatus() == null) ? 0 : getUserStatus().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", userId=").append(userId);
        sb.append(", username=").append(username);
        sb.append(", password=").append(password);
        sb.append(", realName=").append(realName);
        sb.append(", gender=").append(gender);
        sb.append(", mobile=").append(mobile);
        sb.append(", email=").append(email);
        sb.append(", avatar=").append(avatar);
        sb.append(", openId=").append(openId);
        sb.append(", deptId=").append(deptId);
        sb.append(", hireDate=").append(hireDate);
        sb.append(", roleIds=").append(roleIds);
        sb.append(", isSuperAdmin=").append(isSuperAdmin);
        sb.append(", userStatus=").append(userStatus);
        sb.append(", createTime=").append(createTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}