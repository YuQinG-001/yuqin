package com.yuqin.meinian.api.front.VO;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

@Data
public class AppointmentLimitLast60DaysFrontVO implements Serializable {
    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * 预约日期
     */
    private LocalDate appointmentDate;

    /**
     * 实际限流人数
     */
    private Integer actualLimit;

    /**
     * 最大允许人数
     */
    private Integer maxLimit;

    /**
     * 实际预约人数
     */
    private Integer actualCount;

}
