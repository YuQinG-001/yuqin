package com.yuqin.meinian.api.front.DTO;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class FindTop4ByCategoryIdsDTO {
    @NotEmpty(message = "categoryIds不能为空")
    private Integer[] categoryIds;
}
