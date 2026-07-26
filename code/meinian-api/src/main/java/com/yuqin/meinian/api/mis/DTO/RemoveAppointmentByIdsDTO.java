package com.yuqin.meinian.api.mis.DTO;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class RemoveAppointmentByIdsDTO {
    @NotEmpty(message = "ids不能为空")
    private List<Integer> ids;
}