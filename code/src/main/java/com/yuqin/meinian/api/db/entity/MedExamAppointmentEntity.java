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
 * 体检预约记录表
 * @TableName med_exam_appointment
 */
@TableName(value ="med_exam_appointment")
@Data
public class MedExamAppointmentEntity implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 预约编号
     */
    private String appointmentNo;

    /**
     * 关联订单ID
     */
    private Integer orderId;

    /**
     * 预约日期
     */
    private LocalDate appointmentDate;

    /**
     * 患者姓名
     */
    private String patientName;

    /**
     * 性别
     */
    private String gender;

    /**
     * 身份证号
     */
    private String idCardNo;

    /**
     * 出生日期
     */
    private LocalDate birthDate;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 邮寄地址
     */
    private String address;

    /**
     * 工作单位
     */
    private String company;

    /**
     * 状态(1:未签到,2:已签到,3:已完成,4:已取消)
     */
    private Integer status;

    /**
     * 签到时间
     */
    private LocalDateTime checkinTime;

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
        MedExamAppointmentEntity other = (MedExamAppointmentEntity) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getAppointmentNo() == null ? other.getAppointmentNo() == null : this.getAppointmentNo().equals(other.getAppointmentNo()))
            && (this.getOrderId() == null ? other.getOrderId() == null : this.getOrderId().equals(other.getOrderId()))
            && (this.getAppointmentDate() == null ? other.getAppointmentDate() == null : this.getAppointmentDate().equals(other.getAppointmentDate()))
            && (this.getPatientName() == null ? other.getPatientName() == null : this.getPatientName().equals(other.getPatientName()))
            && (this.getGender() == null ? other.getGender() == null : this.getGender().equals(other.getGender()))
            && (this.getIdCardNo() == null ? other.getIdCardNo() == null : this.getIdCardNo().equals(other.getIdCardNo()))
            && (this.getBirthDate() == null ? other.getBirthDate() == null : this.getBirthDate().equals(other.getBirthDate()))
            && (this.getPhone() == null ? other.getPhone() == null : this.getPhone().equals(other.getPhone()))
            && (this.getAddress() == null ? other.getAddress() == null : this.getAddress().equals(other.getAddress()))
            && (this.getCompany() == null ? other.getCompany() == null : this.getCompany().equals(other.getCompany()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getCheckinTime() == null ? other.getCheckinTime() == null : this.getCheckinTime().equals(other.getCheckinTime()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getAppointmentNo() == null) ? 0 : getAppointmentNo().hashCode());
        result = prime * result + ((getOrderId() == null) ? 0 : getOrderId().hashCode());
        result = prime * result + ((getAppointmentDate() == null) ? 0 : getAppointmentDate().hashCode());
        result = prime * result + ((getPatientName() == null) ? 0 : getPatientName().hashCode());
        result = prime * result + ((getGender() == null) ? 0 : getGender().hashCode());
        result = prime * result + ((getIdCardNo() == null) ? 0 : getIdCardNo().hashCode());
        result = prime * result + ((getBirthDate() == null) ? 0 : getBirthDate().hashCode());
        result = prime * result + ((getPhone() == null) ? 0 : getPhone().hashCode());
        result = prime * result + ((getAddress() == null) ? 0 : getAddress().hashCode());
        result = prime * result + ((getCompany() == null) ? 0 : getCompany().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getCheckinTime() == null) ? 0 : getCheckinTime().hashCode());
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
        sb.append(", appointmentNo=").append(appointmentNo);
        sb.append(", orderId=").append(orderId);
        sb.append(", appointmentDate=").append(appointmentDate);
        sb.append(", patientName=").append(patientName);
        sb.append(", gender=").append(gender);
        sb.append(", idCardNo=").append(idCardNo);
        sb.append(", birthDate=").append(birthDate);
        sb.append(", phone=").append(phone);
        sb.append(", address=").append(address);
        sb.append(", company=").append(company);
        sb.append(", status=").append(status);
        sb.append(", checkinTime=").append(checkinTime);
        sb.append(", createTime=").append(createTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}