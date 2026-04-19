package com.yuqin.meinian.api.db.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serial;
import java.io.Serializable;
import lombok.Data;

/**
 * 科室流量调控表
 * @TableName med_department_flow
 */
@TableName(value ="med_department_flow")
@Data
public class MedDepartmentFlowEntity implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 科室名称
     */
    private String departmentName;

    /**
     * 当前人数
     */
    private Integer currentCount;

    /**
     * 最大容量
     */
    private Integer maxCapacity;

    /**
     * 权重值
     */
    private Integer weight;

    /**
     * 优先级
     */
    private Integer priority;

    /**
     * 信标标识
     */
    private String beaconUuid;
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
        MedDepartmentFlowEntity other = (MedDepartmentFlowEntity) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getDepartmentName() == null ? other.getDepartmentName() == null : this.getDepartmentName().equals(other.getDepartmentName()))
            && (this.getCurrentCount() == null ? other.getCurrentCount() == null : this.getCurrentCount().equals(other.getCurrentCount()))
            && (this.getMaxCapacity() == null ? other.getMaxCapacity() == null : this.getMaxCapacity().equals(other.getMaxCapacity()))
            && (this.getWeight() == null ? other.getWeight() == null : this.getWeight().equals(other.getWeight()))
            && (this.getPriority() == null ? other.getPriority() == null : this.getPriority().equals(other.getPriority()))
            && (this.getBeaconUuid() == null ? other.getBeaconUuid() == null : this.getBeaconUuid().equals(other.getBeaconUuid()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getDepartmentName() == null) ? 0 : getDepartmentName().hashCode());
        result = prime * result + ((getCurrentCount() == null) ? 0 : getCurrentCount().hashCode());
        result = prime * result + ((getMaxCapacity() == null) ? 0 : getMaxCapacity().hashCode());
        result = prime * result + ((getWeight() == null) ? 0 : getWeight().hashCode());
        result = prime * result + ((getPriority() == null) ? 0 : getPriority().hashCode());
        result = prime * result + ((getBeaconUuid() == null) ? 0 : getBeaconUuid().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", departmentName=").append(departmentName);
        sb.append(", currentCount=").append(currentCount);
        sb.append(", maxCapacity=").append(maxCapacity);
        sb.append(", weight=").append(weight);
        sb.append(", priority=").append(priority);
        sb.append(", beaconUuid=").append(beaconUuid);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}