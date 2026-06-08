package com.yuqin.meinian.api.mis.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import com.yuqin.meinian.api.common.R;
import com.yuqin.meinian.api.mis.VO.RuleVO;
import com.yuqin.meinian.api.service.PromotionRuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
