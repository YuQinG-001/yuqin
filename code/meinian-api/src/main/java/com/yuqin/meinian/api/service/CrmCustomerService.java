package com.yuqin.meinian.api.service;

import com.github.yulichang.base.MPJBaseService;
import com.yuqin.meinian.api.db.entity.CrmCustomerEntity;
import com.yuqin.meinian.api.front.DTO.ModifyCustomerDTO;
import com.yuqin.meinian.api.front.VO.CustomerLoginVO;
import com.yuqin.meinian.api.front.VO.CustomerUserVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * @author YuQin
 * @description 针对表【crm_customer(客户信息表)】的数据库操作Service
 * @createDate 2026-04-03 02:27:34
 */
public interface CrmCustomerService extends MPJBaseService<CrmCustomerEntity> {
    boolean sendSmsCode(String phone);

    CustomerLoginVO login(String phone, String code);

    CustomerUserVO selectByLoginIdForFront();

    int modify(ModifyCustomerDTO dto);

    String upLoad(MultipartFile file);

    CustomerUserVO findSummary(Integer customerId);
}
