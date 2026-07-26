package com.yuqin.meinian.api.db.mapper;

import com.yuqin.meinian.api.db.entity.MedAppointmentLimitEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yuqin.meinian.api.front.VO.AppointmentLimitLast60DaysFrontVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
* @author YuQin
* @description 针对表【med_appointment_limit(体检预约限流配置表)】的数据库操作Mapper
* @createDate 2026-04-03 02:27:34
* @Entity com.yuqin.meinian.api.db.entity.MedAppointmentLimitEntity
*/
public interface MedAppointmentLimitMapper extends BaseMapper<MedAppointmentLimitEntity> {
    List<AppointmentLimitLast60DaysFrontVO> selectAppointmentLimitLast60Days(Map<String, Object> param);

    List<Map<String, Object>> selectByAppointmentDate(@Param("appointmentDate") String appointmentDate);

    int saveOrUpdateAppointmentLimit(Map<String, Object> param);
}




