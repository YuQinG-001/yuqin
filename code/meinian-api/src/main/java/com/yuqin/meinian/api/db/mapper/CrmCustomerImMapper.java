package com.yuqin.meinian.api.db.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import com.yuqin.meinian.api.db.entity.CrmCustomerImEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.Map;

/**
* @author YuQin
* @description 针对表【crm_customer_im(客户IM账号表)】的数据库操作Mapper
* @createDate 2026-04-03 02:27:34
* @Entity com.yuqin.meinian.api.db.entity.CrmCustomerImEntity
*/
public interface CrmCustomerImMapper extends MPJBaseMapper<CrmCustomerImEntity> {
    /**
     * 保存或更新客户最后一次的登入时间
     * @param customerId 客户ID
     * @return 更新次数
     */
    int insertOrUpdate(Integer customerId);
}




