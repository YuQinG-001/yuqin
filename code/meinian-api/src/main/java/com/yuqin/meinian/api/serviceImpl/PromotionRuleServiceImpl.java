package com.yuqin.meinian.api.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.yuqin.meinian.api.db.entity.PromotionRuleEntity;
import com.yuqin.meinian.api.db.mapper.PromotionRuleMapper;
import com.yuqin.meinian.api.exception.HisException;
import com.yuqin.meinian.api.mis.DTO.RulePageQueryDTO;
import com.yuqin.meinian.api.mis.VO.PromotionRuleStatisticsVO;
import com.yuqin.meinian.api.mis.VO.RuleVO;
import com.yuqin.meinian.api.mis.converter.RuleConvertMapper;
import com.yuqin.meinian.api.service.PromotionRuleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * @author YuQin
 * @description 针对表【promotion_rule(促销规则表)】的数据库操作Service实现
 * @createDate 2026-04-03 02:27:34
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class PromotionRuleServiceImpl extends MPJBaseServiceImpl<PromotionRuleMapper, PromotionRuleEntity>
        implements PromotionRuleService {
    private final RuleConvertMapper ruleConvertMapper;


    @Override
    public List<RuleVO> queryAllRule() {
        LambdaQueryWrapper<PromotionRuleEntity> wrapper = Wrappers.lambdaQuery(PromotionRuleEntity.class);
        wrapper.select(PromotionRuleEntity::getRuleId, PromotionRuleEntity::getRuleName);
        List<PromotionRuleEntity> promotionRuleEntities = baseMapper.selectList(wrapper);
        if (promotionRuleEntities == null) {
            log.warn("查询所有规则返回 null，可能出错");
            return Collections.emptyList();  // 返回空集合，避免 NPE
        }
        if (promotionRuleEntities.isEmpty()) {
            log.info("当前无任何规则数据");
            return Collections.emptyList();
        }
        List<RuleVO> voList = ruleConvertMapper.toVOList(promotionRuleEntities);
        if (voList == null) {
            log.error("MapStruct 转换规则列表返回 null");
            throw new HisException("系统内部错误，规则转换失败");
        }
        log.debug("成功查询规则 {} 条", voList.size());
        return voList;
    }

    @Override
    public IPage<PromotionRuleStatisticsVO> pageQueryByCondition(RulePageQueryDTO dto) {
        Page<PromotionRuleStatisticsVO> page = Page.of(dto.getPageNum(), dto.getPageSize());
        return baseMapper.selectPageByQueryCondition(page, dto.getRuleName());
    }

}




