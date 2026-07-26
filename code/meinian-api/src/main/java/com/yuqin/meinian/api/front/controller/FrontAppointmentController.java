package com.yuqin.meinian.api.front.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.IdcardUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yuqin.meinian.api.common.R;
import com.yuqin.meinian.api.config.satoken.StpCustomerUtil;
import com.yuqin.meinian.api.db.entity.MedExamAppointmentEntity;
import com.yuqin.meinian.api.exception.HisException;
import com.yuqin.meinian.api.front.DTO.PageQueryAppointmentDTO;
import com.yuqin.meinian.api.front.DTO.SaveAppointmentDTO;
import com.yuqin.meinian.api.front.VO.MedExamAppointmentFrontVO;
import com.yuqin.meinian.api.service.MedExamAppointmentService;
import com.yuqin.meinian.api.service.TradeOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/front/appointment")
@RequiredArgsConstructor
public class FrontAppointmentController {
    private final TradeOrderService tradeOrderService;
    private final MedExamAppointmentService appointmentService;

    @PostMapping("/appoint")
    @SaCheckLogin(type = StpCustomerUtil.TYPE)
    public R<String> appoint(@RequestBody @Valid SaveAppointmentDTO form) {
        int customerId = StpCustomerUtil.getLoginIdAsInt();
        Map<String, Object> param = new HashMap<>() {{
            put("customerId", customerId);
            put("orderId", form.getOrderId());
        }};
        boolean bool = tradeOrderService.hasOwnOrder(param);
        if (!bool) {
            throw new HisException("预约失败，该订单与您无关");
        }

        // 验证身份证是否有效
        String idCardNo = form.getIdCardNo();
        if (!IdcardUtil.isValidCard18(idCardNo)) {
            throw new HisException("身份证号码无效");
        }
        LocalDate birthDate = IdcardUtil.getBirthDate(idCardNo).toLocalDateTime().toLocalDate();
        String gender = IdcardUtil.getGenderByIdCard(idCardNo) == 1 ? "男" : "女";

        // 验证日期是否为未来60天以内
        DateTime appointmentDate = DateUtil.parse(form.getAppointmentDate());
        DateTime tomorrow = DateUtil.tomorrow(); //当前时刻的24小时之后
        DateTime startDate = DateUtil.parse(tomorrow.toDateStr()); //明天凌晨
        DateTime endDate = tomorrow.offset(DateField.DAY_OF_MONTH, 60);
        boolean temp = appointmentDate.isIn(startDate, endDate);
        if (!temp) {
            throw new HisException("预约日期错误");
        }
        MedExamAppointmentEntity entity = BeanUtil.toBean(form, MedExamAppointmentEntity.class);
        entity.setAppointmentNo(IdUtil.simpleUUID().toUpperCase());
        entity.setBirthDate(birthDate);
        entity.setGender(gender);
        entity.setStatus(1);
        String appoint = appointmentService.appoint(entity);
        return R.ok(appoint);
    }

    @PostMapping("/page")
    @SaCheckLogin(type = StpCustomerUtil.TYPE)
    public R<IPage<MedExamAppointmentFrontVO>> page(@RequestBody @Valid PageQueryAppointmentDTO form) {
        IPage<MedExamAppointmentFrontVO> pageResult = appointmentService.pageQuery(form);
        return R.ok(pageResult);
    }


}
