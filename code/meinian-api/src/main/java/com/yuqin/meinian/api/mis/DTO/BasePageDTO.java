package com.yuqin.meinian.api.mis.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

@Data
@Schema(description = "分页请求基础参数")
public class BasePageDTO {

    @Schema(description = "当前页码（从1开始），默认1", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "pageNum不能为空")
    @Min(value = 1, message = "pageNum不能小于1")
    private Integer pageNum = 1;

    @Schema(description = "每页条数（10~50），默认10", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "pageSize不能为空")
    @Range(min = 10, max = 50, message = "pageSize必须在10~50之间")
    private Integer pageSize = 10;
}