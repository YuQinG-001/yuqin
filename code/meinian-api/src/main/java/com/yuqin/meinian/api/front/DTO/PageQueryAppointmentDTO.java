package com.yuqin.meinian.api.front.DTO;

import com.yuqin.meinian.api.common.BasePageDTO;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Range;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class PageQueryAppointmentDTO extends BasePageDTO implements Serializable {
    // 为什么上面没有添加校验规则？因为这个不需要前端提交。从SATOKEN当中获取的。
    private Integer customerId;

    @Pattern(regexp = "^[a-zA-Z0-9\\u4e00-\\u9fa5]{1,50}$", message = "keyword内容不正确")
    private String keyword;

    @Pattern(regexp = "^1$|^2$|^3$|^4$", message = "status内容不正确")
    private String status;

    @Pattern(regexp = "^((((1[6-9]|[2-9]\\d)\\d{2})-(0?[13578]|1[02])-(0?[1-9]|[12]\\d|3[01]))|(((1[6-9]|[2-9]\\d)\\d{2})-(0?[13456789]|1[012])-(0?[1-9]|[12]\\d|30))|(((1[6-9]|[2-9]\\d)\\d{2})-0?2-(0?[1-9]|1\\d|2[0-8]))|(((1[6-9]|[2-9]\\d)(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))-0?2-29))$", message = "appointmentDate内容不正确")
    private String appointmentDate;
}