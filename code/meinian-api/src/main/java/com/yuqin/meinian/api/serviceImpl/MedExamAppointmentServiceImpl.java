package com.yuqin.meinian.api.serviceImpl;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdcardUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.toolkit.JoinWrappers;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.yuqin.meinian.api.common.MinIO;
import com.yuqin.meinian.api.config.satoken.StpCustomerUtil;
import com.yuqin.meinian.api.db.entity.CheckupItem;
import com.yuqin.meinian.api.db.entity.MedExamAppointmentEntity;
import com.yuqin.meinian.api.db.entity.MedExamReportEntity;
import com.yuqin.meinian.api.db.entity.TradeOrderEntity;
import com.yuqin.meinian.api.db.mapper.MedAppointmentLimitMapper;
import com.yuqin.meinian.api.db.mapper.MedExamAppointmentMapper;
import com.yuqin.meinian.api.db.mapper.MedExamReportMapper;
import com.yuqin.meinian.api.db.mapper.TradeOrderMapper;
import com.yuqin.meinian.api.db.mongo.CheckupResultDao;
import com.yuqin.meinian.api.db.mongo.GoodsSnapshotDao;
import com.yuqin.meinian.api.exception.HisException;
import com.yuqin.meinian.api.front.DTO.PageQueryAppointmentDTO;
import com.yuqin.meinian.api.front.VO.MedExamAppointmentFrontVO;
import com.yuqin.meinian.api.mis.DTO.CheckinAppointmentDTO;
import com.yuqin.meinian.api.mis.DTO.PageQueryAppointmentForMisDTO;
import com.yuqin.meinian.api.mis.VO.MedExamAppointmentMisVO;
import com.yuqin.meinian.api.service.MedExamAppointmentService;
import com.yuqin.meinian.api.util.FaceAuthUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/**
 * @author YuQin
 * @description 针对表【med_exam_appointment(体检预约记录表)】的数据库操作Service实现
 * @createDate 2026-04-03 02:27:34
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MedExamAppointmentServiceImpl extends MPJBaseServiceImpl<MedExamAppointmentMapper, MedExamAppointmentEntity> implements MedExamAppointmentService {
    private final FaceAuthUtil faceAuthUtil;

    private final MinIO minIO;

    private final GoodsSnapshotDao goodsSnapshotDao;

    private final CheckupResultDao checkupResultDao;

    private final RedisTemplate<String, Object> redisTemplate;
    public static final String APPOINTMENT_FULL = "当天预约已满，请选择其它日期";
    public static final String APPOINTMENT_FAIL = "预约失败";
    public static final String APPOINTMENT_SUCCESS = "预约成功";
    private final MedExamAppointmentMapper medExamAppointmentMapper;
    private final MedAppointmentLimitMapper medAppointmentLimitMapper;
    private final TradeOrderMapper tradeOrderMapper;
    private final MedExamReportMapper medExamReportMapper;

    @Override
    public List<MedExamAppointmentMisVO> findByOrderId(Integer orderId) {
        return baseMapper.selectByOrderId(orderId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String appoint(MedExamAppointmentEntity entity) {
        // 拼接redis的key
        String key = "appointment#" + entity.getAppointmentDate();
        String execute = redisTemplate.execute(new AppointSessionCallback(key));
        if (!execute.equals(APPOINTMENT_SUCCESS)) {
            return execute;
        }
        try {
            if (medExamAppointmentMapper.insert(entity) <= 0) {
                log.error("redis中的{}actualCount成功加1，但是保存预约记录失败", key);
                throw new HisException("保存预约记录失败");
            }
            Map<String, Object> map = MapUtil.newHashMap();
            map.put("appointmentDate", entity.getAppointmentDate());
            Integer actualLimit = MapUtil.getInt(redisTemplate.opsForHash().entries(key), "actualLimit");
            map.put("actualLimit", actualLimit);
            map.put("maxLimit", actualLimit);
            if (medAppointmentLimitMapper.saveOrUpdateAppointmentLimit(map) <= 0) {
                log.error("redis中的{}actualCount成功加1，保存预约记录成功，但是更新/保存限流规则失败", key);
                throw new HisException("更新/保存限流规则失败");
            }
            Map<String, Object> map2 = MapUtil.newHashMap();
            map2.put("orderStatus", 5);
            map2.put("orderId", entity.getOrderId());
            if (tradeOrderMapper.updateStatus(map2) <= 0) {
                log.error("redis中的{}actualCount成功加1，保存预约记录成功，更新/保存限流规则失败，但是更新订单状态失败", key);
                throw new HisException("新订单状态失败");
            }
        } catch (Exception e) {
            //回滚
            redisTemplate.execute(new RollbackAppointSessionCallback(key));
            throw new HisException(e);
        }
        return execute;

    }

    static class RollbackAppointSessionCallback implements SessionCallback<String> {
        private final String key;

        public RollbackAppointSessionCallback(String key) {
            this.key = key;
        }

        @Override
        @SuppressWarnings(value = "unchecked")
        public @Nullable String execute(@NonNull RedisOperations operations) throws DataAccessException {
            boolean rollbackSuccess = false;
            //重试机制
            for (int i = 0; i < 3 && !rollbackSuccess; i++) {
                operations.watch(key);
                operations.multi();
                operations.opsForHash().increment(key, "actualCount", -1);
                List<Object> exec = operations.exec();
                if (!exec.isEmpty()) {
                    rollbackSuccess = true;
                }
                // 每次重试时稍微等一会
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
            if (!rollbackSuccess) {
                // 补偿表机制，将失败写入补偿表，后续人为处理
            }
            return null;
        }
    }

    static class AppointSessionCallback implements SessionCallback<String> {
        private final String key;

        public AppointSessionCallback(String key) {
            this.key = key;
        }

        @Override
        @SuppressWarnings("unchecked")
        public @NonNull String execute(@NonNull RedisOperations operations) throws DataAccessException {
            operations.watch(key);
            Map<String, Object> entries = operations.opsForHash().entries(key);
            int actualCount = MapUtil.getInt(entries, "actualCount");
            int actualLimit = MapUtil.getInt(entries, "actualLimit");
            if (actualCount >= actualLimit) {
                operations.unwatch();
                return APPOINTMENT_FULL;
            }
            operations.multi();
            operations.opsForHash().increment(key, "actualCount", 1);
            List<Object> results = operations.exec();
            if (results.isEmpty()) {
                return APPOINTMENT_FAIL;
            }
            return APPOINTMENT_SUCCESS;
        }
    }

    @Override
    public IPage<MedExamAppointmentFrontVO> pageQuery(PageQueryAppointmentDTO form) {
        Page<MedExamAppointmentFrontVO> page = Page.of(form.getPageNum(), form.getPageSize());
        int customerId = StpCustomerUtil.getLoginIdAsInt();
        form.setCustomerId(customerId);
        return this.selectJoinListPage(page, MedExamAppointmentFrontVO.class, buildWrapper(form));
    }

    private MPJLambdaWrapper<MedExamAppointmentEntity> buildWrapper(PageQueryAppointmentDTO form) {
        return JoinWrappers.lambda(MedExamAppointmentEntity.class)
                // 关联订单表
                .select(MedExamAppointmentEntity::getPatientName)
                .select(MedExamAppointmentEntity::getAppointmentDate)
                .select(MedExamAppointmentEntity::getStatus)
                .select(TradeOrderEntity::getGoodsTitle)
                .select(MedExamReportEntity::getFileUrl)
                // 内连接订单表
                .innerJoin(TradeOrderEntity.class, TradeOrderEntity::getOrderId, MedExamAppointmentEntity::getOrderId)

                // 左连接报告表
                .leftJoin(MedExamReportEntity.class, MedExamReportEntity::getAppointmentId, MedExamAppointmentEntity::getId)

                // 查询条件
                .eq(TradeOrderEntity::getCustomerId, form.getCustomerId())

                // 关键字搜索
                .and(StrUtil.isNotBlank(form.getKeyword()), wrapper1 -> wrapper1.like(TradeOrderEntity::getGoodsTitle, form.getKeyword())
                        .or()
                        .eq(MedExamAppointmentEntity::getPatientName, form.getKeyword())
                        .or()
                        .eq(MedExamAppointmentEntity::getPhone, form.getKeyword()))

                // 预约日期条件
                .eq(ObjectUtil.isNotNull(form.getAppointmentDate()), MedExamAppointmentEntity::getAppointmentDate, form.getAppointmentDate())

                // 状态条件
                .eq(ObjectUtil.isNotNull(form.getStatus()), MedExamAppointmentEntity::getStatus, form.getStatus())

                // 排序
                .orderByDesc(MedExamAppointmentEntity::getAppointmentDate);
    }

    @Override
    public IPage<MedExamAppointmentMisVO> pageQueryByCondition(PageQueryAppointmentForMisDTO dto) {
        Page<Object> page = Page.of(dto.getPageNum(), dto.getPageSize());
        return baseMapper.selectPageForMis(page, dto.getAppointmentDate(), dto.getPatientName(), dto.getStatus(), dto.getPhone());
    }

    @Override
    public int deleteByIdsForMis(List<Integer> ids) {
        LambdaQueryWrapper<MedExamAppointmentEntity> wrapper = Wrappers.lambdaQuery(MedExamAppointmentEntity.class)
                .in(MedExamAppointmentEntity::getId, ids);
        return baseMapper.delete(wrapper);
    }

    @Override
    public int hasAppointmentInToday(Map<String, Object> param) {
        Map<String, Object> map = baseMapper.selectAppointInToday(param);
        if (map == null) {
            // 没有预约
            return 0;
        } else if (MapUtil.getInt(map, "status") != 1) {
            // 有预约已签到
            return -1;
        } else {
            // 有预约未签到
            return 1;
        }
    }

    @Override
    public Map<String, Object> findGuidanceInfo(Integer id) {
        // 根据ID从数据库查询预约概要信息
        Map<String, Object> map = medExamAppointmentMapper.selectSummaryById(id);

        // 从查询结果中获取快照ID、性别和预约编号
        String snapshotId = MapUtil.getStr(map, "snapshotId");
        String gender = MapUtil.getStr(map, "gender");
        String appointmentNo = MapUtil.getStr(map, "appointmentNo");

        // 配置二维码生成参数
        QrConfig qrConfig = new QrConfig();
        qrConfig.setWidth(100);      // 设置二维码宽度
        qrConfig.setHeight(100);     // 设置二维码高度
        qrConfig.setMargin(0);       // 设置二维码边距

        // 生成二维码并转换为Base64格式（体检流水号做二维码）
        String qrCodeBase64 = QrCodeUtil.generateAsBase64(appointmentNo, qrConfig, "jpg");
        // 将二维码Base64字符串添加到返回结果中
        map.put("qrCodeBase64", qrCodeBase64);

        // 根据快照ID和性别查询体检项目信息
        List<CheckupItem> list = goodsSnapshotDao.searchCheckup(snapshotId, gender);

        // 使用LinkedHashSet去重并保持顺序
        LinkedHashSet<Map> set = new LinkedHashSet<>();

        // 遍历体检项目列表，提取关键信息并去重
        list.forEach(one -> {
            HashMap temp = new HashMap<>() {{
                // 只保留地点和名称信息用于去重比较
                put("place", one.getPlace());
                put("name", one.getName());
            }};
            set.add(temp);
        });

        // 将去重后的体检项目信息添加到返回结果中
        map.put("checkup", set);

        return map;
    }

    @Override
    @Transactional
    public boolean checkin(CheckinAppointmentDTO dto) {
        // 从参数中提取患者基本信息
        String idCardNo = dto.getIdCardNo();
        String patientName = dto.getPatientName();
        // 根据身份证号解析性别（1为男，其他为女）
        String gender = IdcardUtil.getGenderByIdCard(idCardNo) == 1 ? "男" : "女";
        String photo_1 = dto.getPhoto_1();
        String photo_2 = dto.getPhoto_2();

        // 调用人脸认证工具进行人脸比对验证
        boolean result = faceAuthUtil.verifyFaceModel(patientName, idCardNo, gender, photo_1, photo_2);

        // 如果人脸认证通过，执行签到流程
        if (result) {
            // 生成唯一的图片文件名：身份证号_时间戳.jpg
            String filename = idCardNo + "_" + System.currentTimeMillis() + ".jpg";
            String path = "checkin/" + filename;

            // 将签到照片上传到MinIO对象存储
            minIO.uploadImage(path, photo_2);

            // 更新预约状态为已签到
            int rows = medExamAppointmentMapper.updateForCheckin(dto);
            if (rows != 1) {
                throw new HisException("保存签到记录失败");
            }

            // 查询预约编号和快照ID
            Map<String, Object> map = medExamAppointmentMapper.selectAppointNoAndSnapshotId(dto);
            String appointmentNo = MapUtil.getStr(map, "appointmentNo");
            String snapshotId = MapUtil.getStr(map, "snapshotId");

            // 根据快照ID和性别查询对应的体检项目
            List<CheckupItem> checkup = goodsSnapshotDao.searchCheckup(snapshotId, gender);

            // 为患者初始化体检结果记录
            boolean bool = checkupResultDao.insert(appointmentNo, checkup);
            if (!bool) {
                throw new HisException("添加体检结果失败");
            }
        }

        // 返回人脸认证结果
        return result;
    }
    @Override
    @Transactional
    public boolean modifyStatusByAppointmentNo(Map<String, Object> param) {
        int rows = medExamAppointmentMapper.updateStatusByAppointmentNo(param);
        if (rows != 1) {
            return false;
        }
        int status = MapUtil.getInt(param, "status");
        if (status == 3) {
            //检查对应的订单是否所有体检预约都已经结束
            String appointmentNo = MapUtil.getStr(param, "appointmentNo");
            Map<String,Object> map = tradeOrderMapper.selectOrderIsFinished(appointmentNo);
            int orderId = MapUtil.getInt(map, "orderId");
            int n1 = MapUtil.getInt(map, "n1");
            int n2 = MapUtil.getInt(map, "n2");
            if (n1 == n2) {
                //更新订单为已结束状态
                rows = tradeOrderMapper.updateStatus(new HashMap<>() {{
                    put("orderStatus", 6);
                    put("orderId", orderId);
                }});
                if (rows != 1) {
                    return false;
                }
            }
            //查询体检结果ID
            String reportId = checkupResultDao.selectIdByAppointmentNo(appointmentNo);
            param.put("reportId", reportId);
            rows = medExamReportMapper.insert(param);
            return rows == 1;
        }
        return true;
    }
}




