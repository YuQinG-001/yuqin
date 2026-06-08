package com.yuqin.meinian.api.mis.converter;


import com.yuqin.meinian.api.db.entity.PromotionRuleEntity;
import com.yuqin.meinian.api.mis.VO.RuleVO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RuleConvertMapper {
    RuleVO toVO(PromotionRuleEntity entity);
    List<RuleVO> toVOList(List<PromotionRuleEntity> entityList);
}
