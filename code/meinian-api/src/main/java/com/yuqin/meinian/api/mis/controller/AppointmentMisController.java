package com.yuqin.meinian.api.mis.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yuqin.meinian.api.common.R;
import com.yuqin.meinian.api.mis.DTO.*;
import com.yuqin.meinian.api.mis.VO.MedExamAppointmentMisVO;
import com.yuqin.meinian.api.service.MedExamAppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/mis/appointment")
@Validated
@RequiredArgsConstructor
public class AppointmentMisController {
    private final MedExamAppointmentService medExamAppointmentService;

    @GetMapping("/findByOrderId")
    @SaCheckPermission(value = {"ROOT", "APPOINTMENT:SELECT"}, mode = SaMode.OR)
    public R<List<MedExamAppointmentMisVO>> findByOrderId(@RequestParam Integer orderId) {
        List<MedExamAppointmentMisVO> list = medExamAppointmentService.findByOrderId(orderId);
        return R.ok(list);
    }

    @PostMapping("/pageQuery")
    @SaCheckPermission(value = {"ROOT", "APPOINTMENT:SELECT"}, mode = SaMode.OR)
    public R<IPage<MedExamAppointmentMisVO>> pageQuery(@RequestBody @Valid PageQueryAppointmentForMisDTO form) {
        IPage<MedExamAppointmentMisVO> pageResult = medExamAppointmentService.pageQueryByCondition(form);
        return R.ok(pageResult);
    }

    @PostMapping("deleteByIds")
    @SaCheckPermission(value = {"ROOT", "APPOINTMENT:DELETE"}, mode = SaMode.OR)
    public R<Integer> deleteByIdsForMis(@RequestBody RemoveAppointmentByIdsDTO dto) {
        int i = medExamAppointmentService.deleteByIdsForMis(dto.getIds());
        return R.ok(i);
    }
    @PostMapping("/hasInToday")
    @SaCheckPermission(value = {"ROOT", "APPOINTMENT:UPDATE"}, mode = SaMode.OR)
    public R<Integer> hasInToday(@RequestBody @Valid HasAppointmentInTodayDTO form) {
        Map<String, Object> param = BeanUtil.beanToMap(form);
        int result = medExamAppointmentService.hasAppointmentInToday(param);
        return R.ok(result);
    }
    @PostMapping("/checkin")
    @SaCheckPermission(value = {"ROOT", "APPOINTMENT:UPDATE"}, mode = SaMode.OR)
    public R<Boolean> checkin(@RequestBody @Valid CheckinAppointmentDTO form) {
        boolean result = medExamAppointmentService.checkin(form);
        return R.ok(result);
    }

    @PostMapping("/findGuidanceInfo")
    @SaCheckPermission(value = {"ROOT", "APPOINTMENT:SELECT"}, mode = SaMode.OR)
    public R<Map<String, Object>> findGuidanceInfo(@RequestBody @Valid FindGuidanceInfoDTO form) {
        Map<String, Object> map = medExamAppointmentService.findGuidanceInfo(form.getId());
        return R.ok(map);
    }

    @PostMapping("/modifyStatusByAppointmentNo")
    @SaCheckPermission(value = {"ROOT", "APPOINTMENT:UPDATE"}, mode = SaMode.OR)
    public R<Boolean> modifyStatusByAppointmentNo(@RequestBody @Valid ModifyStatusByAppointmentNoDTO form) {
        Map<String, Object> param = BeanUtil.beanToMap(form);
        boolean result = medExamAppointmentService.modifyStatusByAppointmentNo(param);
        return R.ok(result);
    }
}
