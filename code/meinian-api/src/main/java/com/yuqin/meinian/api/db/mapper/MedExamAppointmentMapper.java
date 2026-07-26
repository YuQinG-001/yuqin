package com.yuqin.meinian.api.db.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseMapper;
import com.yuqin.meinian.api.db.entity.MedExamAppointmentEntity;
import com.yuqin.meinian.api.mis.DTO.CheckinAppointmentDTO;
import com.yuqin.meinian.api.mis.VO.MedExamAppointmentMisVO;

import java.util.List;
import java.util.Map;

/**
 * @author YuQin
 * @description 针对表【med_exam_appointment(体检预约记录表)】的数据库操作Mapper
 * @createDate 2026-04-03 02:27:34
 * @Entity com.yuqin.meinian.api.db.entity.MedExamAppointmentEntity
 */
public interface MedExamAppointmentMapper extends MPJBaseMapper<MedExamAppointmentEntity> {
    List<MedExamAppointmentMisVO> selectByOrderId(Integer orderId);

    IPage<MedExamAppointmentMisVO> selectPageForMis(Page<?> page, String appointmentDate, String patientName, Integer status, String phone);

    Map<String, Object> selectAppointInToday(Map<String, Object> param);

    int updateForCheckin(CheckinAppointmentDTO dto);

    Map<String, Object> selectAppointNoAndSnapshotId(CheckinAppointmentDTO dto);

    Map<String, Object> selectSummaryById(Integer id);

    int updateStatusByAppointmentNo(Map<String, Object> param);
}




