package com.yuqin.meinian.api.mis.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yuqin.meinian.api.common.R;
import com.yuqin.meinian.api.db.entity.PromotionRuleEntity;
import com.yuqin.meinian.api.mis.DTO.RulePageQueryDTO;
import com.yuqin.meinian.api.mis.DTO.SavePromotionRuleDTO;
import com.yuqin.meinian.api.mis.VO.PromotionRuleStatisticsVO;
import com.yuqin.meinian.api.mis.VO.RuleVO;
import com.yuqin.meinian.api.service.PromotionRuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RequiredArgsConstructor
@RestController
@Tag(name = "权限管理", description = "")
@RequestMapping("/mis/rule")
public class PromotionRuleController {
    private final PromotionRuleService promotionRuleService;

    @SaCheckLogin
    @SaCheckPermission(value = {"ROOT", "RULE:SELECT"}, mode = SaMode.OR)
    @Operation(summary = "查询所有规则", description = "获取全部规则的ID和名称列表")
    @GetMapping("/queryAllRule")
    public R<List<RuleVO>> queryAllRule() {
        List<RuleVO> ruleVOS = promotionRuleService.queryAllRule();
        return R.ok(ruleVOS);
    }

    @SaCheckLogin
    @SaCheckPermission(value = {"ROOT", "RULE:SELECT"}, mode = SaMode.OR)
    @PostMapping("/pageQuery")
    public R<IPage<PromotionRuleStatisticsVO>> pageQuery(@RequestBody @Valid RulePageQueryDTO dto) {
        IPage<PromotionRuleStatisticsVO> promotionRuleStatisticsVOIPage = promotionRuleService.pageQueryByCondition(dto);
        return R.ok(promotionRuleStatisticsVOIPage);
    }

    @PostMapping("/save")
    @SaCheckPermission(value = {"ROOT", "RULE:INSERT"}, mode = SaMode.OR)
    public R<Boolean> save(@RequestBody @Valid SavePromotionRuleDTO dto) {
        // 将form转换为bean
        PromotionRuleEntity promotionRuleEntity = BeanUtil.toBean(dto, PromotionRuleEntity.class);
        return R.ok(promotionRuleService.save(promotionRuleEntity));
    }
}
