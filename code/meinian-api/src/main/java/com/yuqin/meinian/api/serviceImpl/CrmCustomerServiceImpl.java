package com.yuqin.meinian.api.serviceImpl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yulichang.toolkit.MPJWrappers;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.yuqin.meinian.api.common.MinIO;
import com.yuqin.meinian.api.config.satoken.StpCustomerUtil;
import com.yuqin.meinian.api.db.entity.CrmCustomerEntity;
import com.yuqin.meinian.api.db.entity.TradeOrderEntity;
import com.yuqin.meinian.api.db.mapper.CrmCustomerMapper;
import com.yuqin.meinian.api.db.mapper.TradeOrderMapper;
import com.yuqin.meinian.api.exception.HisException;
import com.yuqin.meinian.api.front.DTO.ModifyCustomerDTO;
import com.yuqin.meinian.api.front.VO.CustomerLoginVO;
import com.yuqin.meinian.api.front.VO.CustomerUserVO;
import com.yuqin.meinian.api.service.CrmCustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * @author YuQin
 * @description 针对表【crm_customer(客户信息表)】的数据库操作Service实现
 * @createDate 2026-04-03 02:27:34
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CrmCustomerServiceImpl extends ServiceImpl<CrmCustomerMapper, CrmCustomerEntity> implements CrmCustomerService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final TradeOrderMapper              tradeOrderMapper;
    private final MinIO minIO;

    @Override
    public boolean sendSmsCode(String phone) {
        //设置Key为SMS_CODE_手机号(1分钟内拒绝重复发送）
        String keyRefresh = "SMS_CODE_REFRESH_" + phone;
        if (redisTemplate.hasKey(keyRefresh)) {
            return false;
        }
//        生成一个6位数随机验证码
        String code = RandomUtil.randomNumbers(6);
        log.info("发送验证码：{}", code);
        redisTemplate.opsForValue().set(keyRefresh, code);
        redisTemplate.expire(keyRefresh, 1, TimeUnit.MINUTES);
//        设置Key为SMS_CODE_手机号(5分钟,用于发送验证）
        String key = "SMS_CODE_" + phone;
        redisTemplate.opsForValue().set(key, code);
//        设置有效期5分钟
        redisTemplate.expire(key, 5, TimeUnit.MINUTES);
//        调用运营的发送短信接口
        log.info("发送验证码：{} 到手机：{}", code, phone);
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int modify(ModifyCustomerDTO dto) {
        int id = StpCustomerUtil.getLoginIdAsInt();
        return baseMapper.updateById(CrmCustomerEntity.builder()
                .id(id)
                .customerName(dto.getCustomerName())
                .phone(dto.getPhone())
                .gender(dto.getGender())
                .photoUrl(dto.getPhotoUrl())
                .build());


    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String upLoad(MultipartFile file) {
//        randomUUID存在"-",使用simpleUUID
        String filename = IdUtil.simpleUUID() + ".jpg";
        String path = "/front/goods/img/" + filename;
        minIO.uploadImage(path, file);
        return path;
    }

    @Override
    public CustomerUserVO selectByLoginIdForFront() {
        int id = StpCustomerUtil.getLoginIdAsInt();
        // 1. 查询客户基本信息
        CrmCustomerEntity crmCustomerEntity = baseMapper.selectById(id);
        if (crmCustomerEntity == null) throw new HisException("请重新登入");
        // 2. 查询订单统计
        MPJLambdaWrapper<TradeOrderEntity> wrapper = MPJWrappers.lambdaJoin(TradeOrderEntity.class)
                .selectSum(TradeOrderEntity::getTotalAmount, "totalAmount")
                .selectCount(TradeOrderEntity::getOrderId, "totalCount")
                .selectSum(TradeOrderEntity::getQuantity, "totalQuantity")
                .eq(TradeOrderEntity::getCustomerId, id)
                .in(TradeOrderEntity::getOrderStatus, Arrays.asList(3, 5, 6));
        CustomerUserVO statistics = tradeOrderMapper.selectJoinOne(CustomerUserVO.class, wrapper);
        // 3. 合并
        return CustomerUserVO.builder()
                .id(crmCustomerEntity.getId())
                .customerName(crmCustomerEntity.getCustomerName())
                .phone(crmCustomerEntity.getPhone())
                .gender(crmCustomerEntity.getGender())
                .photoUrl(crmCustomerEntity.getPhotoUrl())
                .registerTime(crmCustomerEntity.getRegisterTime())
                .totalAmount(statistics != null && statistics.getTotalAmount() != null ? statistics.getTotalAmount()
                                                                                       : BigDecimal.ZERO)
                .totalCount(statistics != null && statistics.getTotalCount() != null ? statistics.getTotalCount() : 0)
                .totalQuantity(
                        statistics != null && statistics.getTotalQuantity() != null ? statistics.getTotalQuantity() : 0)
                .build();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public CustomerLoginVO login(String phone, String code) {
        String key = "SMS_CODE_" + phone;
        String keyRefresh = "SMS_CODE_REFRESH_" + phone;
        // 用 sms_code_ 和 手机号 进行字符串拼接，得到key
        if (!redisTemplate.hasKey(key)) {
            throw new HisException("手机号验证码已过期");
        }
        // 程序能够执行到此处，说明验证码没有过期。接下来看看验证码是否正确。
        if (!code.equals(redisTemplate.opsForValue().get(key))) {
            throw new HisException("验证码错误");
        }
        // 程序执行到这里说明手机号和验证码都有效。先清空redis中的两个key
        redisTemplate.delete(key);
        redisTemplate.delete(keyRefresh);
        // 判断该用户是否已经注册
        LambdaQueryWrapper<CrmCustomerEntity> eq = Wrappers.lambdaQuery(CrmCustomerEntity.class)
                .select(CrmCustomerEntity::getId)
                .eq(CrmCustomerEntity::getPhone, phone);
        CrmCustomerEntity existCustomer = baseMapper.selectOne(eq);
        Integer id = existCustomer != null ? existCustomer.getId() : null;
        // 证明用户不存在，则需要保存用户信息。
        if (id == null) {
            CrmCustomerEntity crmCustomerEntity = new CrmCustomerEntity();
            crmCustomerEntity.setPhone(phone);
            int insertNum = baseMapper.insert(crmCustomerEntity);
            if (insertNum <= 0) {
                throw new HisException("注册失败");
            }
            // 获取到保存之后生成的主键值
            id = crmCustomerEntity.getId();
        }
        StpCustomerUtil.login(id, "PC");
        String tokenValue = StpCustomerUtil.getTokenValue();
        return CustomerLoginVO.builder().Id(id).token(tokenValue).build();
    }


}




