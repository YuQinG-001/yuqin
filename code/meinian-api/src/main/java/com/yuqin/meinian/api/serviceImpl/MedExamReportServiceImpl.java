package com.yuqin.meinian.api.serviceImpl;

import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.toolkit.JoinWrappers;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.yuqin.meinian.api.db.entity.MedExamPackageEntity;
import com.yuqin.meinian.api.db.entity.MedExamReportEntity;
import com.yuqin.meinian.api.db.entity.PromotionRuleEntity;
import com.yuqin.meinian.api.db.mapper.MedExamReportMapper;
import com.yuqin.meinian.api.mis.VO.PackageWithRuleVO;
import com.yuqin.meinian.api.service.MedExamReportService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author YuQin
 * @description 针对表【med_exam_report(体检报告表)】的数据库操作Service实现
 * @createDate 2026-04-03 02:27:34
 */
@Service
public class MedExamReportServiceImpl extends MPJBaseServiceImpl<MedExamReportMapper, MedExamReportEntity>
        implements MedExamReportService {
}




