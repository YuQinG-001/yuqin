package com.yuqin.meinian.api.db.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 体检套餐表
 * @TableName med_exam_package
 */
@TableName(value ="med_exam_package")
@Data
public class MedExamPackageEntity implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 套餐编码
     */
    private String packageCode;

    /**
     * 套餐名称
     */
    private String packageName;

    /**
     * 套餐描述
     */
    private String description;

    /**
     * 科室检查项目
     */
    private String departmentExam;

    /**
     * 实验室检查项目
     */
    private String labExam;

    /**
     * 医技检查项目
     */
    private String medicalExam;

    /**
     * 其他检查项目
     */
    private String otherExam;

    /**
     * 详细检查项
     */
    private String examItems;

    /**
     * 封面图片
     */
    private String coverImage;

    /**
     * 原价
     */
    private BigDecimal originalPrice;

    /**
     * 现价
     */
    private BigDecimal currentPrice;

    /**
     * 销量
     */
    private Integer salesVolume;

    /**
     * 套餐类型
     */
    private String packageType;

    /**
     * 标签
     */
    private String tags;

    /**
     * 分类ID(1:活动专区,2:热卖套餐,3:新品推荐,4:孝敬父母,5:白领精英)
     */
    private Integer categoryId;

    /**
     * 促销活动ID
     */
    private Integer promotionId;

    /**
     * 状态(1:上架,0:下架)
     */
    private Integer status;

    /**
     * 数据校验值
     */
    private String md5Hash;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

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
        MedExamPackageEntity other = (MedExamPackageEntity) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getPackageCode() == null ? other.getPackageCode() == null : this.getPackageCode().equals(other.getPackageCode()))
            && (this.getPackageName() == null ? other.getPackageName() == null : this.getPackageName().equals(other.getPackageName()))
            && (this.getDescription() == null ? other.getDescription() == null : this.getDescription().equals(other.getDescription()))
            && (this.getDepartmentExam() == null ? other.getDepartmentExam() == null : this.getDepartmentExam().equals(other.getDepartmentExam()))
            && (this.getLabExam() == null ? other.getLabExam() == null : this.getLabExam().equals(other.getLabExam()))
            && (this.getMedicalExam() == null ? other.getMedicalExam() == null : this.getMedicalExam().equals(other.getMedicalExam()))
            && (this.getOtherExam() == null ? other.getOtherExam() == null : this.getOtherExam().equals(other.getOtherExam()))
            && (this.getExamItems() == null ? other.getExamItems() == null : this.getExamItems().equals(other.getExamItems()))
            && (this.getCoverImage() == null ? other.getCoverImage() == null : this.getCoverImage().equals(other.getCoverImage()))
            && (this.getOriginalPrice() == null ? other.getOriginalPrice() == null : this.getOriginalPrice().equals(other.getOriginalPrice()))
            && (this.getCurrentPrice() == null ? other.getCurrentPrice() == null : this.getCurrentPrice().equals(other.getCurrentPrice()))
            && (this.getSalesVolume() == null ? other.getSalesVolume() == null : this.getSalesVolume().equals(other.getSalesVolume()))
            && (this.getPackageType() == null ? other.getPackageType() == null : this.getPackageType().equals(other.getPackageType()))
            && (this.getTags() == null ? other.getTags() == null : this.getTags().equals(other.getTags()))
            && (this.getCategoryId() == null ? other.getCategoryId() == null : this.getCategoryId().equals(other.getCategoryId()))
            && (this.getPromotionId() == null ? other.getPromotionId() == null : this.getPromotionId().equals(other.getPromotionId()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getMd5Hash() == null ? other.getMd5Hash() == null : this.getMd5Hash().equals(other.getMd5Hash()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getPackageCode() == null) ? 0 : getPackageCode().hashCode());
        result = prime * result + ((getPackageName() == null) ? 0 : getPackageName().hashCode());
        result = prime * result + ((getDescription() == null) ? 0 : getDescription().hashCode());
        result = prime * result + ((getDepartmentExam() == null) ? 0 : getDepartmentExam().hashCode());
        result = prime * result + ((getLabExam() == null) ? 0 : getLabExam().hashCode());
        result = prime * result + ((getMedicalExam() == null) ? 0 : getMedicalExam().hashCode());
        result = prime * result + ((getOtherExam() == null) ? 0 : getOtherExam().hashCode());
        result = prime * result + ((getExamItems() == null) ? 0 : getExamItems().hashCode());
        result = prime * result + ((getCoverImage() == null) ? 0 : getCoverImage().hashCode());
        result = prime * result + ((getOriginalPrice() == null) ? 0 : getOriginalPrice().hashCode());
        result = prime * result + ((getCurrentPrice() == null) ? 0 : getCurrentPrice().hashCode());
        result = prime * result + ((getSalesVolume() == null) ? 0 : getSalesVolume().hashCode());
        result = prime * result + ((getPackageType() == null) ? 0 : getPackageType().hashCode());
        result = prime * result + ((getTags() == null) ? 0 : getTags().hashCode());
        result = prime * result + ((getCategoryId() == null) ? 0 : getCategoryId().hashCode());
        result = prime * result + ((getPromotionId() == null) ? 0 : getPromotionId().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getMd5Hash() == null) ? 0 : getMd5Hash().hashCode());
        result = prime * result + ((getUpdateTime() == null) ? 0 : getUpdateTime().hashCode());
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
        sb.append(", packageCode=").append(packageCode);
        sb.append(", packageName=").append(packageName);
        sb.append(", description=").append(description);
        sb.append(", departmentExam=").append(departmentExam);
        sb.append(", labExam=").append(labExam);
        sb.append(", medicalExam=").append(medicalExam);
        sb.append(", otherExam=").append(otherExam);
        sb.append(", examItems=").append(examItems);
        sb.append(", coverImage=").append(coverImage);
        sb.append(", originalPrice=").append(originalPrice);
        sb.append(", currentPrice=").append(currentPrice);
        sb.append(", salesVolume=").append(salesVolume);
        sb.append(", packageType=").append(packageType);
        sb.append(", tags=").append(tags);
        sb.append(", categoryId=").append(categoryId);
        sb.append(", promotionId=").append(promotionId);
        sb.append(", status=").append(status);
        sb.append(", md5Hash=").append(md5Hash);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", createTime=").append(createTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}