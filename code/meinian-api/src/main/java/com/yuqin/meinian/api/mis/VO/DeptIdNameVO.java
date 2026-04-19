package com.yuqin.meinian.api.mis.VO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "部门ID和名称响应对象")
public class DeptIdNameVO {
    @Schema(description = "部门ID")
    private String deptId;
    @Schema(description = "部门名称")
    private String deptName;
}
