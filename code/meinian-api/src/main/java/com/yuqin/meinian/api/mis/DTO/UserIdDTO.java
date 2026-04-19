package com.yuqin.meinian.api.mis.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserIdDTO {

    @Schema(description = "User表的主键", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "userId不能为空")
    @Min(value = 0, message = "userId不能小于0")
    private Integer userId;
}
