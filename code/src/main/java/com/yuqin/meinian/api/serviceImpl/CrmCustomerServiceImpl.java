package com.yuqin.meinian.api.serviceImpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuqin.meinian.api.db.entity.CrmCustomerEntity;
import com.yuqin.meinian.api.service.CrmCustomerService;
import com.yuqin.meinian.api.db.mapper.CrmCustomerMapper;
import org.springframework.stereotype.Service;

/**
* @author YuQin
* @description 针对表【crm_customer(客户信息表)】的数据库操作Service实现
* @createDate 2026-04-02 20:29:13
*/
@Service
public class CrmCustomerServiceImpl extends ServiceImpl<CrmCustomerMapper, CrmCustomerEntity>
    implements CrmCustomerService{

}




