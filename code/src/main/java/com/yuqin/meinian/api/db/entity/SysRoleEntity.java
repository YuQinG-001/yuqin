package com.yuqin.meinian.api.db.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serial;
import java.io.Serializable;
import lombok.Data;

/**
 * 系统角色表
 * @TableName sys_role
 */
@TableName(value ="sys_role")
@Data
public class SysRoleEntity implements Serializable {
    /**
     * 角色ID
     */
    @TableId(type = IdType.AUTO)
    private Integer roleId;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色描述
     */
    private String roleDesc;

    /**
     * 权限ID集合
     */
    private String permissionIds;

    /**
     * 默认权限集合
     */
    private String defaultPermissions;

    /**
     * 是否系统内置角色(0:否,1:是)
     */
    private Integer isSystem;
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
        SysRoleEntity other = (SysRoleEntity) that;
        return (this.getRoleId() == null ? other.getRoleId() == null : this.getRoleId().equals(other.getRoleId()))
            && (this.getRoleName() == null ? other.getRoleName() == null : this.getRoleName().equals(other.getRoleName()))
            && (this.getRoleDesc() == null ? other.getRoleDesc() == null : this.getRoleDesc().equals(other.getRoleDesc()))
            && (this.getPermissionIds() == null ? other.getPermissionIds() == null : this.getPermissionIds().equals(other.getPermissionIds()))
            && (this.getDefaultPermissions() == null ? other.getDefaultPermissions() == null : this.getDefaultPermissions().equals(other.getDefaultPermissions()))
            && (this.getIsSystem() == null ? other.getIsSystem() == null : this.getIsSystem().equals(other.getIsSystem()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getRoleId() == null) ? 0 : getRoleId().hashCode());
        result = prime * result + ((getRoleName() == null) ? 0 : getRoleName().hashCode());
        result = prime * result + ((getRoleDesc() == null) ? 0 : getRoleDesc().hashCode());
        result = prime * result + ((getPermissionIds() == null) ? 0 : getPermissionIds().hashCode());
        result = prime * result + ((getDefaultPermissions() == null) ? 0 : getDefaultPermissions().hashCode());
        result = prime * result + ((getIsSystem() == null) ? 0 : getIsSystem().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", roleId=").append(roleId);
        sb.append(", roleName=").append(roleName);
        sb.append(", roleDesc=").append(roleDesc);
        sb.append(", permissionIds=").append(permissionIds);
        sb.append(", defaultPermissions=").append(defaultPermissions);
        sb.append(", isSystem=").append(isSystem);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}