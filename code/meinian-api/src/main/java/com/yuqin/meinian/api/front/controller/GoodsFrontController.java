package com.yuqin.meinian.api.front.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yuqin.meinian.api.common.R;
import com.yuqin.meinian.api.front.DTO.FindTop4ByCategoryIdsDTO;
import com.yuqin.meinian.api.front.DTO.MedExamPackagePageQueryDTO;
import com.yuqin.meinian.api.front.VO.ExamPackageDetailForFrontVO;
import com.yuqin.meinian.api.front.VO.MedExamPackagePageQueryVO;
import com.yuqin.meinian.api.front.VO.Top4CustomerFrontVO;
import com.yuqin.meinian.api.mis.DTO.ModifyMedExamPackageDTO;
import com.yuqin.meinian.api.mis.VO.ExamPackageDetailVO;
import com.yuqin.meinian.api.service.MedExamPackageService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/front/goods")
public class GoodsFrontController {
    private final MedExamPackageService medExamPackageService;

    @GetMapping
    @Operation(summary = "修改体检数据", description = "修改体检套餐页面数据")
    public R<ExamPackageDetailForFrontVO> findGoodsById(@RequestParam @Valid Integer id) {
        ExamPackageDetailForFrontVO examPackageDetailVO = medExamPackageService.queryExamPackageDetailForFront(id);
        return R.ok(examPackageDetailVO);
    }

    @PostMapping("/findTop4ByCategoryIds")
    public R<Map<Integer, List<Top4CustomerFrontVO>>> findTop4ByCategoryIds(@RequestBody @Valid FindTop4ByCategoryIdsDTO form) {
        Map<Integer, List<Top4CustomerFrontVO>> map = medExamPackageService.findTop4ByCategoryIdOrderBySalesDesc(form.getCategoryIds());
        return R.ok(map);
    }
    @PostMapping("/pageQuery")
    public R<IPage<MedExamPackagePageQueryVO>> pageQueryByCondition(@RequestBody @Valid MedExamPackagePageQueryDTO dto) {
        IPage<MedExamPackagePageQueryVO> medExamPackagePageQueryVOIPage = medExamPackageService.pageQueryByCondition(dto);
        return R.ok(medExamPackagePageQueryVOIPage);
    }

}
