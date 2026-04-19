package com.yuqin.meinian.api.db.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serial;
import java.io.Serializable;
import lombok.Data;

/**
 * 系统操作类型表
 * @TableName sys_operation_type
 */
@TableName(value ="sys_operation_type")
@Data
public class SysOperationTypeEntity implements Serializable {
    /**
     * 主键ID
     */
    @TableId
    private Integer id;

    /**
     * 操作编码
     */
    private String operationCode;

    /**
     * 操作名称
     */
    private String operationName;
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
        SysOperationTypeEntity other = (SysOperationTypeEntity) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getOperationCode() == null ? other.getOperationCode() == null : this.getOperationCode().equals(other.getOperationCode()))
            && (this.getOperationName() == null ? other.getOperationName() == null : this.getOperationName().equals(other.getOperationName()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getOperationCode() == null) ? 0 : getOperationCode().hashCode());
        result = prime * result + ((getOperationName() == null) ? 0 : getOperationName().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", operationCode=").append(operationCode);
        sb.append(", operationName=").append(operationName);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}