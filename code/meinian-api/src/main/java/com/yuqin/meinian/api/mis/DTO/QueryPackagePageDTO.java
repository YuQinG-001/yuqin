package com.yuqin.meinian.api.mis.DTO;

import com.yuqin.meinian.api.common.BasePageDTO;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Range;


@Data
@EqualsAndHashCode(callSuper = true)
public class QueryPackagePageDTO extends BasePageDTO {


    @Pattern(regexp = "^[a-zA-Z0-9\\u4e00-\\u9fa5]{1,50}$", message = "packageName内容不正确")
    private String packageName;

    @Pattern(regexp = "^[a-zA-Z0-9]{6,20}$", message = "packageCode内容不正确")
    private String packageCode;

    @Pattern(regexp = "^父母体检$|^入职体检$|^职场白领$|^个人高端$|^中青年体检$", message = "packageType内容不正确")
    private String packageType;

    @Range(min = 1, max = 5, message = "categoryId范围不正确")
    private Byte categoryId;

    private Boolean status;

}
