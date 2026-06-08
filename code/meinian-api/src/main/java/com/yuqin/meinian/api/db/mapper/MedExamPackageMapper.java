package com.yuqin.meinian.api.db.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import com.yuqin.meinian.api.BO.ExamPackageDetailBO;
import com.yuqin.meinian.api.db.entity.MedExamPackageEntity;
import com.yuqin.meinian.api.mis.VO.ExamPackageDetailVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author YuQin
 * @description 针对表【med_exam_package(体检套餐表)】的数据库操作Mapper
 * @createDate 2026-04-03 02:27:34
 * @Entity com.yuqin.meinian.api.db.entity.MedExamPackageEntity
 */
public interface MedExamPackageMapper extends MPJBaseMapper<MedExamPackageEntity> {

    ExamPackageDetailBO selectByIdWithStatus(@Param("id") Integer id, @Param("status") Integer status);
}





