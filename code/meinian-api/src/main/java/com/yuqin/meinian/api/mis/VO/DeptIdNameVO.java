package com.yuqin.meinian.api.mis.VO;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@Schema(description = "部门ID和名称响应对象")
public class DeptIdNameVO implements Serializable {
    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @Schema(description = "部门ID")
    private String deptId;
    @Schema(description = "部门名称")
    private String deptName;
}
