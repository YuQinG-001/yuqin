package com.yuqin.meinian.api.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import com.yuqin.meinian.api.db.entity.MedExamAppointmentEntity;
import com.yuqin.meinian.api.front.DTO.PageQueryAppointmentDTO;
import com.yuqin.meinian.api.front.VO.MedExamAppointmentFrontVO;
import com.yuqin.meinian.api.mis.DTO.CheckinAppointmentDTO;
import com.yuqin.meinian.api.mis.DTO.PageQueryAppointmentForMisDTO;
import com.yuqin.meinian.api.mis.VO.MedExamAppointmentMisVO;

import java.util.List;
import java.util.Map;

/**
* @author YuQin
* @description 针对表【med_exam_appointment(体检预约记录表)】的数据库操作Service
* @createDate 2026-04-03 02:27:34
*/
public interface MedExamAppointmentService extends MPJBaseService<MedExamAppointmentEntity> {
    List<MedExamAppointmentMisVO> findByOrderId(Integer orderId);
    String appoint(MedExamAppointmentEntity entity);
    IPage<MedExamAppointmentFrontVO> pageQuery(PageQueryAppointmentDTO form);

    IPage<MedExamAppointmentMisVO> pageQueryByCondition(PageQueryAppointmentForMisDTO dto);
    int deleteByIdsForMis(List<Integer> ids);
    int hasAppointmentInToday(Map<String, Object> param);
    boolean checkin(CheckinAppointmentDTO dto);
    boolean modifyStatusByAppointmentNo(Map<String, Object> param);
    Map<String, Object> findGuidanceInfo(Integer id);
}
