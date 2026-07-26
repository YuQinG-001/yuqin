package com.yuqin.meinian.api.mis.DTO;

import com.yuqin.meinian.api.common.BasePageDTO;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Range;

@EqualsAndHashCode(callSuper = true)
@Data
public class RulePageQueryDTO extends BasePageDTO {
    @Pattern(regexp = "^[0-9a-zA-Z\\u4e00-\\u9fa5]{1,20}$", message = "ruleName内容不正确")
    private String ruleName;

}

