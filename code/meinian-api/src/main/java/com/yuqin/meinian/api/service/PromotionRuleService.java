package com.yuqin.meinian.api.service;

import com.github.yulichang.base.MPJBaseService;
import com.yuqin.meinian.api.db.entity.PromotionRuleEntity;
import com.yuqin.meinian.api.mis.VO.RuleVO;

import java.util.List;

/**
 * @author YuQin
 * @description 针对表【promotion_rule(促销规则表)】的数据库操作Service
 * @createDate 2026-04-03 02:27:34
 */
public interface PromotionRuleService extends MPJBaseService<PromotionRuleEntity> {
    List<RuleVO> queryAllRule();
}
