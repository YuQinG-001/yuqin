package com.yuqin.meinian.api.serviceImpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuqin.meinian.api.db.entity.MedExamAppointmentEntity;
import com.yuqin.meinian.api.service.MedExamAppointmentService;
import com.yuqin.meinian.api.db.mapper.MedExamAppointmentMapper;
import org.springframework.stereotype.Service;

/**
* @author YuQin
* @description 针对表【med_exam_appointment(体检预约记录表)】的数据库操作Service实现
* @createDate 2026-04-02 20:29:13
*/
@Service
public class MedExamAppointmentServiceImpl extends ServiceImpl<MedExamAppointmentMapper, MedExamAppointmentEntity>
    implements MedExamAppointmentService{

}




