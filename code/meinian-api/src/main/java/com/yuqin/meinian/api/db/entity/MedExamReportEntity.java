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
 * 体检报告表
 * @TableName med_exam_report
 */
@TableName(value ="med_exam_report")
@Data
public class MedExamReportEntity implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 预约记录ID
     */
    private Integer appointmentId;

    /**
     * 报告ID(MongoDB)
     */
    private String reportId;

    /**
     * 报告状态(1:未生成,2:已生成,3:已邮寄)
     */
    private Integer status;

    /**
     * 报告文件URL
     */
    private String fileUrl;

    /**
     * 快递单号
     */
    private String expressNo;

    /**
     * 邮寄日期
     */
    private LocalDate expressDate;

    /**
     * 体检日期
     */
    private LocalDate examDate;

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
        MedExamReportEntity other = (MedExamReportEntity) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getAppointmentId() == null ? other.getAppointmentId() == null : this.getAppointmentId().equals(other.getAppointmentId()))
            && (this.getReportId() == null ? other.getReportId() == null : this.getReportId().equals(other.getReportId()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getFileUrl() == null ? other.getFileUrl() == null : this.getFileUrl().equals(other.getFileUrl()))
            && (this.getExpressNo() == null ? other.getExpressNo() == null : this.getExpressNo().equals(other.getExpressNo()))
            && (this.getExpressDate() == null ? other.getExpressDate() == null : this.getExpressDate().equals(other.getExpressDate()))
            && (this.getExamDate() == null ? other.getExamDate() == null : this.getExamDate().equals(other.getExamDate()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getAppointmentId() == null) ? 0 : getAppointmentId().hashCode());
        result = prime * result + ((getReportId() == null) ? 0 : getReportId().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getFileUrl() == null) ? 0 : getFileUrl().hashCode());
        result = prime * result + ((getExpressNo() == null) ? 0 : getExpressNo().hashCode());
        result = prime * result + ((getExpressDate() == null) ? 0 : getExpressDate().hashCode());
        result = prime * result + ((getExamDate() == null) ? 0 : getExamDate().hashCode());
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
        sb.append(", appointmentId=").append(appointmentId);
        sb.append(", reportId=").append(reportId);
        sb.append(", status=").append(status);
        sb.append(", fileUrl=").append(fileUrl);
        sb.append(", expressNo=").append(expressNo);
        sb.append(", expressDate=").append(expressDate);
        sb.append(", examDate=").append(examDate);
        sb.append(", createTime=").append(createTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}