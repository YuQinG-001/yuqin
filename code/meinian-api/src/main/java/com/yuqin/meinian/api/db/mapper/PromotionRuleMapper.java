package com.yuqin.meinian.api.db.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseMapper;
import com.yuqin.meinian.api.db.entity.PromotionRuleEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yuqin.meinian.api.mis.VO.PromotionRuleStatisticsVO;

/**
* @author YuQin
* @description 针对表【promotion_rule(促销规则表)】的数据库操作Mapper
* @createDate 2026-04-03 02:27:34
* @Entity com.yuqin.meinian.api.db.entity.PromotionRuleEntity
*/
public interface PromotionRuleMapper extends MPJBaseMapper<PromotionRuleEntity> {
    IPage<PromotionRuleStatisticsVO> selectPageByQueryCondition(Page<?> page, String ruleName);
}




