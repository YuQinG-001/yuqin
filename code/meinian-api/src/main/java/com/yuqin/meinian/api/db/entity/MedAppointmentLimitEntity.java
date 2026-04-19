package com.yuqin.meinian.api.db.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 体检预约限流配置表
 * @TableName med_appointment_limit
 */
@TableName(value ="med_appointment_limit")
@Data
public class MedAppointmentLimitEntity implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 预约日期
     */
    private LocalDate appointmentDate;

    /**
     * 实际限流人数
     */
    private Integer actualLimit;

    /**
     * 最大允许人数
     */
    private Integer maxLimit;

    /**
     * 实际预约人数
     */
    private Integer actualCount;

    /**
     * 备注信息
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

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
        MedAppointmentLimitEntity other = (MedAppointmentLimitEntity) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getAppointmentDate() == null ? other.getAppointmentDate() == null : this.getAppointmentDate().equals(other.getAppointmentDate()))
            && (this.getActualLimit() == null ? other.getActualLimit() == null : this.getActualLimit().equals(other.getActualLimit()))
            && (this.getMaxLimit() == null ? other.getMaxLimit() == null : this.getMaxLimit().equals(other.getMaxLimit()))
            && (this.getActualCount() == null ? other.getActualCount() == null : this.getActualCount().equals(other.getActualCount()))
            && (this.getRemark() == null ? other.getRemark() == null : this.getRemark().equals(other.getRemark()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getAppointmentDate() == null) ? 0 : getAppointmentDate().hashCode());
        result = prime * result + ((getActualLimit() == null) ? 0 : getActualLimit().hashCode());
        result = prime * result + ((getMaxLimit() == null) ? 0 : getMaxLimit().hashCode());
        result = prime * result + ((getActualCount() == null) ? 0 : getActualCount().hashCode());
        result = prime * result + ((getRemark() == null) ? 0 : getRemark().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", appointmentDate=").append(appointmentDate);
        sb.append(", actualLimit=").append(actualLimit);
        sb.append(", maxLimit=").append(maxLimit);
        sb.append(", actualCount=").append(actualCount);
        sb.append(", remark=").append(remark);
        sb.append(", createTime=").append(createTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}