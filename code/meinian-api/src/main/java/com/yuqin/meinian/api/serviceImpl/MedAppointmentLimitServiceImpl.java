package com.yuqin.meinian.api.serviceImpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuqin.meinian.api.db.entity.MedAppointmentLimitEntity;
import com.yuqin.meinian.api.service.MedAppointmentLimitService;
import com.yuqin.meinian.api.db.mapper.MedAppointmentLimitMapper;
import org.springframework.stereotype.Service;

/**
* @author YuQin
* @description 针对表【med_appointment_limit(体检预约限流配置表)】的数据库操作Service实现
* @createDate 2026-04-03 02:27:34
*/
@Service
public class MedAppointmentLimitServiceImpl extends ServiceImpl<MedAppointmentLimitMapper, MedAppointmentLimitEntity>
    implements MedAppointmentLimitService{

}




