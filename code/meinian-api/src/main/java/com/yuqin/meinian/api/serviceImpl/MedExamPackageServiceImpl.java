package com.yuqin.meinian.api.serviceImpl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.MD5;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.toolkit.JoinWrappers;
import com.github.yulichang.toolkit.MPJWrappers;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.yuqin.meinian.api.BO.ExamPackageDetailBO;
import com.yuqin.meinian.api.common.CryptoUtil;
import com.yuqin.meinian.api.common.MinIO;
import com.yuqin.meinian.api.db.entity.ExaminationDetail;
import com.yuqin.meinian.api.db.entity.GoodsSnapshotEntity;
import com.yuqin.meinian.api.db.entity.MedExamPackageEntity;
import com.yuqin.meinian.api.db.entity.PromotionRuleEntity;
import com.yuqin.meinian.api.db.mapper.MedExamPackageMapper;
import com.yuqin.meinian.api.db.mongo.GoodsSnapshotDao;
import com.yuqin.meinian.api.exception.HisException;
import com.yuqin.meinian.api.front.DTO.MedExamPackagePageQueryDTO;
import com.yuqin.meinian.api.front.VO.ExamPackageDetailForFrontVO;
import com.yuqin.meinian.api.front.VO.MedExamPackagePageQueryVO;
import com.yuqin.meinian.api.front.VO.Top4CustomerFrontVO;
import com.yuqin.meinian.api.mis.DTO.*;
import com.yuqin.meinian.api.mis.VO.ExamPackageDetailVO;
import com.yuqin.meinian.api.mis.VO.PackageWithRuleVO;
import com.yuqin.meinian.api.mis.converter.MedExamPackageConvertMapper;
import com.yuqin.meinian.api.service.MedExamPackageService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jspecify.annotations.NonNull;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author YuQin
 * @description 针对表【med_exam_package(体检套餐表)】的数据库操作Service实现
 * @createDate 2026-04-03 02:27:34
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MedExamPackageServiceImpl extends MPJBaseServiceImpl<MedExamPackageMapper, MedExamPackageEntity>
        implements MedExamPackageService {
    private static final MD5 MD5_INSTANCE = CryptoUtil.MD5_INSTANCE;

    private final MinIO minIO;

    private final MedExamPackageMapper medExamPackageMapper;

    private final MedExamPackageConvertMapper medExamPackageConvertMapper;
    private final GoodsSnapshotDao goodsSnapshotDao;

    public IPage<PackageWithRuleVO> queryPackageWithRulePage(QueryPackagePageDTO dto) {
        IPage<PackageWithRuleVO> packagePageWithRule = getPackagePageWithRule(dto);
        return packagePageWithRule;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "goods", key = "#id")
    public String upLoadExcel(Integer id, MultipartFile file) {
        String filename = id + ".xlsx";
        String path = "/front/goods/excel/" + filename;
        minIO.uploadExcel(path, file);
        List<ExaminationDetail> list = new ArrayList<>();
        try (BufferedInputStream in = new BufferedInputStream(file.getInputStream())) {
            XSSFWorkbook book = new XSSFWorkbook(in);
            XSSFSheet sheet = book.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                XSSFRow row = sheet.getRow(i);
                ExaminationDetail detail = new ExaminationDetail();
                detail.setPlace(getCellString(row, 0));
                detail.setName(getCellString(row, 1));
                detail.setItem(getCellString(row, 2));
                detail.setType(getCellString(row, 3));
                detail.setCode(getCellString(row, 4));
                detail.setSex(getCellString(row, 5));
                detail.setValue(getCellString(row, 6));
                detail.setTemplate(getCellString(row, 7));
                list.add(detail);
            }
        } catch (Exception e) {
            throw new HisException("解析Excel出错了", e);
        }
        MedExamPackageEntity entity = baseMapper.selectById(id);
        entity.setExamItems(list);
        String s = generateMd5Hash(entity);
        LambdaUpdateWrapper<MedExamPackageEntity> wrapper = Wrappers.lambdaUpdate(MedExamPackageEntity.class);
        wrapper.eq(MedExamPackageEntity::getId, id)
                .set(MedExamPackageEntity::getMd5Hash, s)
                .set(MedExamPackageEntity::getExamItems, list, "typeHandler=" + JacksonTypeHandler.class.getName());
        baseMapper.update(wrapper);
        return path;
    }

    // 辅助方法：安全获取单元格字符串（处理null、数字等）
    private String getCellString(XSSFRow row, int index) {
        XSSFCell cell = row.getCell(index);
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf(cell.getNumericCellValue());
            default -> null;
        };
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
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "goods", key = "#dto.id")
    public int modifyStatus(ModifyStatusDTO dto) {
        Wrapper<MedExamPackageEntity> wrapper = Wrappers.lambdaUpdate(MedExamPackageEntity.class)
                .eq(MedExamPackageEntity::getId, dto.getId())
                .set(MedExamPackageEntity::getStatus, dto.getStatus());
        int update = medExamPackageMapper.update(new MedExamPackageEntity(), wrapper);
        if (update == 0) {
            throw new HisException("更新上下架状态失败");
        }
        return update;
    }

    @Override
    public Map<Integer, List<Top4CustomerFrontVO>> findTop4ByCategoryIdOrderBySalesDesc(Integer[] categoryIds) {
        Map<Integer, List<Top4CustomerFrontVO>> map = new HashMap<>();
        for (Integer categoryId : categoryIds) {
            MPJLambdaWrapper<MedExamPackageEntity> wrapper = MPJWrappers.lambdaJoin(MedExamPackageEntity.class)
                    .select(MedExamPackageEntity::getId,
                            MedExamPackageEntity::getCurrentPrice,
                            MedExamPackageEntity::getPackageCode,
                            MedExamPackageEntity::getPackageName,
                            MedExamPackageEntity::getCoverImage,
                            MedExamPackageEntity::getDescription,
                            MedExamPackageEntity::getOriginalPrice,
                            MedExamPackageEntity::getSalesVolume)
                    .eq(MedExamPackageEntity::getCategoryId, categoryId)
                    .eq(MedExamPackageEntity::getStatus, 1)
                    .orderByDesc(MedExamPackageEntity::getSalesVolume, MedExamPackageEntity::getId)
                    .last("limit 4");
            List<Top4CustomerFrontVO> top4CustomerFrontVOS = baseMapper.selectJoinList(Top4CustomerFrontVO.class, wrapper);
            map.put(categoryId, top4CustomerFrontVOS);
        }
        return map;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "goods", key = "#ids")
    public int removeByIds(List<Integer> ids) {
        List<String> list = this.queryCoverImage(ids);
        //先删除数据库
        Wrapper<MedExamPackageEntity> wrapper = Wrappers.lambdaQuery(MedExamPackageEntity.class)
                .eq(MedExamPackageEntity::getStatus, 0)
                .and(w -> w.eq(MedExamPackageEntity::getSalesVolume, 0)
                        .or().isNull(MedExamPackageEntity::getSalesVolume))
                .in(MedExamPackageEntity::getId, ids);
        int delete = baseMapper.delete(wrapper);
        if (delete == 0) {
            throw new HisException("删除失败");
        }
        if (CollUtil.isEmpty(list)) {
            return delete;
        }
        //在删除MinIO文件，MinIO不支持回滚的
        try {
            minIO.removeFile(list);
        } catch (Exception e) {
            log.error("数据库已删但MinIO文件删除失败，ids: {}，文件列表: {}，需人工清理", ids, list, e);
        }
        return delete;
    }

    @Override
    public List<String> queryCoverImage(List<Integer> ids) {
        Wrapper<MedExamPackageEntity> wrapper = Wrappers.lambdaQuery(MedExamPackageEntity.class)
                .select(MedExamPackageEntity::getCoverImage)
                .eq(MedExamPackageEntity::getStatus, 0)
                .and(w -> w.eq(MedExamPackageEntity::getSalesVolume, 0)
                        .or().isNull(MedExamPackageEntity::getSalesVolume))
                .in(MedExamPackageEntity::getId, ids);
        return baseMapper.selectList(wrapper)
                .stream()
                .map(MedExamPackageEntity::getCoverImage)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void download(int id, HttpServletResponse response) {

        String filename = id + ".xlsx";
        String path = "/front/goods/excel/" + filename;
        if (!minIO.doesObjectExist(path)) {
            throw new HisException("该套餐尚未上传体检内容文档或文件错误，请先上传");
        }
        //设置下载文件的名称
        response.setHeader("Content-Disposition", "attachment; filename=" + id + ".xlsx");
        //该MIME类型会让浏览器弹出下载对话框
        response.setContentType("application/x-download");
        response.setCharacterEncoding("UTF-8");

        try (
                BufferedInputStream bis = new BufferedInputStream(minIO.downloadFile(path));
                OutputStream os = response.getOutputStream();
                BufferedOutputStream bos = new BufferedOutputStream(os)
        ) {
            IoUtil.copy(bis, bos);
            bos.flush();
        } catch (IOException e) {
            throw new HisException("文档下载失败", e);
        }
    }

    private IPage<PackageWithRuleVO> getPackagePageWithRule(@NonNull QueryPackagePageDTO dto) {
        MPJLambdaWrapper<MedExamPackageEntity> wrapper = JoinWrappers.lambda("t", MedExamPackageEntity.class)
                .select(MedExamPackageEntity::getId,
                        MedExamPackageEntity::getPackageName,
                        MedExamPackageEntity::getPackageCode,
                        MedExamPackageEntity::getOriginalPrice,
                        MedExamPackageEntity::getCurrentPrice,
                        MedExamPackageEntity::getSalesVolume,
                        MedExamPackageEntity::getPackageType,
                        MedExamPackageEntity::getStatus,
                        MedExamPackageEntity::getCategoryId)
                .selectAs(PromotionRuleEntity::getRuleName, PackageWithRuleVO::getRuleName)
                .selectAs("(t.exam_items IS NOT NULL)", PackageWithRuleVO::getHasCheckup)
                .leftJoin(PromotionRuleEntity.class, PromotionRuleEntity::getRuleId, MedExamPackageEntity::getPromotionId)
                .like(StrUtil.isNotBlank(dto.getPackageName()), MedExamPackageEntity::getPackageName,
                        dto.getPackageName())
                .eq(StrUtil.isNotBlank(dto.getPackageCode()), MedExamPackageEntity::getPackageCode,
                        dto.getPackageCode())
                .eq(Objects.nonNull(dto.getPackageType()), MedExamPackageEntity::getPackageType, dto.getPackageType())
                .eq(Objects.nonNull(dto.getCategoryId()), MedExamPackageEntity::getCategoryId, dto.getCategoryId())
                .eq(Objects.nonNull(dto.getStatus()), MedExamPackageEntity::getStatus, dto.getStatus())
                .orderByAsc(MedExamPackageEntity::getId);

        Page<PackageWithRuleVO> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        return baseMapper.selectJoinPage(page, PackageWithRuleVO.class, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "goods", key = "#dto.id")
    public int modifyMedExamPackage(ModifyMedExamPackageDTO dto) {
        MedExamPackageEntity entity = medExamPackageConvertMapper.toEntity(dto);
        entity.setMd5Hash(generateMd5Hash(entity));
        return baseMapper.updateById(entity);
    }

    @Override
    public ExamPackageDetailVO queryExamPackageDetail(QueryExamPackageDetailDTO dto) {
        ExamPackageDetailBO examPackageDetailBO = medExamPackageMapper.selectByIdWithStatus(dto.getId(), null);
        return BeanUtil.copyProperties(examPackageDetailBO, ExamPackageDetailVO.class);
    }

    @Override
    @Cacheable(cacheNames = "goods", key = "#id")
    public ExamPackageDetailForFrontVO queryExamPackageDetailForFront(int id) {
        ExamPackageDetailBO examPackageDetailBO = medExamPackageMapper.selectByIdWithStatus(id, 1);
        return BeanUtil.copyProperties(examPackageDetailBO, ExamPackageDetailForFrontVO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int saveMedExamPackage(@NonNull SaveMedExamPackageDTO dto) {
        MedExamPackageEntity entity = medExamPackageConvertMapper.toEntity(dto);
        entity.setMd5Hash(generateMd5Hash(entity));
        return baseMapper.insert(entity);
    }

    private String generateMd5Hash(MedExamPackageEntity entity) {
        JSONObject json = JSONUtil.parseObj(entity);
        json.remove("id");
        json.remove("categoryId");
        json.remove("salesVolume");
        json.remove("status");
        json.remove("md5Hash");
        json.remove("updateTime");
        json.remove("createTime");
        return MD5_INSTANCE.digestHex(json.toString().toUpperCase());
    }

    @Override
    public IPage<MedExamPackagePageQueryVO> pageQueryByCondition(MedExamPackagePageQueryDTO dto) {
        MPJLambdaWrapper<MedExamPackageEntity> wrapper = JoinWrappers.lambda(MedExamPackageEntity.class)
                .select(MedExamPackageEntity::getPackageCode,
                        MedExamPackageEntity::getPackageName,
                        MedExamPackageEntity::getDescription,
                        MedExamPackageEntity::getCoverImage,
                        MedExamPackageEntity::getCurrentPrice,
                        MedExamPackageEntity::getOriginalPrice,
                        MedExamPackageEntity::getSalesVolume,
                        MedExamPackageEntity::getId
                )
                .eq(MedExamPackageEntity::getStatus, 1)
                .eq(StrUtil.isNotBlank(dto.getPackageType()), MedExamPackageEntity::getPackageType, dto.getPackageType());
        if (StrUtil.isNotBlank(dto.getKeyword())) {
            wrapper.and(w -> w
                    .like(MedExamPackageEntity::getPackageCode, dto.getKeyword())
                    .or()
                    .like(MedExamPackageEntity::getPackageName, dto.getKeyword())
            );
        }
        if (StrUtil.isNotBlank(dto.getSex())) {
            wrapper.apply("JSON_CONTAINS(tags, CONCAT('\"', {0}, '\"'))", dto.getSex());
        }
        if (null != dto.getPriceType()) {
            switch (dto.getPriceType()) {
                case 1 -> wrapper.ge(MedExamPackageEntity::getCurrentPrice, 0)
                        .lt(MedExamPackageEntity::getCurrentPrice, 100);
                case 2 -> wrapper.ge(MedExamPackageEntity::getCurrentPrice, 100)
                        .lt(MedExamPackageEntity::getCurrentPrice, 500);
                case 3 -> wrapper.ge(MedExamPackageEntity::getCurrentPrice, 500)
                        .lt(MedExamPackageEntity::getCurrentPrice, 1000);
                case 4 -> wrapper.ge(MedExamPackageEntity::getCurrentPrice, 1000);
                default -> {
                }
            }
        }
        if (null != dto.getOrderType() && !dto.getOrderType().isEmpty()) {
            if (dto.getOrderType().contains(1)) {
                wrapper.orderByDesc(MedExamPackageEntity::getId);
            }
            if (dto.getOrderType().contains(2)) {
                wrapper.orderByDesc(MedExamPackageEntity::getSalesVolume);
            }
            if (dto.getOrderType().contains(3)) {
                wrapper.orderByAsc(MedExamPackageEntity::getCurrentPrice);
            }
            if (dto.getOrderType().contains(4)) {
                wrapper.orderByDesc(MedExamPackageEntity::getCurrentPrice);
            }
        } else wrapper.orderByDesc(MedExamPackageEntity::getSalesVolume).orderByDesc(MedExamPackageEntity::getId);
        // 分页参数友好处理
        if (dto.getPageNo() == null || dto.getPageNo() < 1) {
            dto.setPageNo(1);
        }
        if (dto.getPageSize() == null || dto.getPageSize() < 1) {
            dto.setPageSize(10);
        }
        Page<MedExamPackagePageQueryVO> page = Page.of(dto.getPageNo(), dto.getPageSize());
        return baseMapper.selectJoinPage(page, MedExamPackagePageQueryVO.class, wrapper);
    }

    @Override
    public GoodsSnapshotEntity findPackageAndPromotionById(Integer id) {
        MPJLambdaWrapper<MedExamPackageEntity> wrapper = MPJWrappers.lambdaJoin(MedExamPackageEntity.class)
                .select(MedExamPackageEntity::getId,
                        MedExamPackageEntity::getPackageName,
                        MedExamPackageEntity::getDescription,
                        MedExamPackageEntity::getCoverImage,
                        MedExamPackageEntity::getPackageCode,
                        MedExamPackageEntity::getDepartmentExam,
                        MedExamPackageEntity::getLabExam,
                        MedExamPackageEntity::getMedicalExam,
                        MedExamPackageEntity::getOtherExam,
                        MedExamPackageEntity::getOriginalPrice,
                        MedExamPackageEntity::getCurrentPrice,
                        MedExamPackageEntity::getPackageType,
                        MedExamPackageEntity::getTags,
                        MedExamPackageEntity::getExamItems,
                        MedExamPackageEntity::getMd5Hash
                )
                .select(PromotionRuleEntity::getRuleName, PromotionRuleEntity::getRuleContent)
                .leftJoin(PromotionRuleEntity.class, PromotionRuleEntity::getRuleId, MedExamPackageEntity::getPromotionId)
                .eq(MedExamPackageEntity::getId, id);
        return baseMapper.selectJoinOne(GoodsSnapshotEntity.class, wrapper);

    }

    @Override
    public GoodsSnapshotEntity findBySnapshotId(String snapshotId, Integer customerId) {
        // TODO 业务端用户在使用这个功能的时候，必须提供一个customerId，因为系统要保证当前客户只能看自己的商品快照，
        //  不能说客户只要有一个快照id就可以随便看别人的商品快照。判断逻辑是只要该客户拥有该快照id就可以查看，后面实现,
        //  mis端的用户不需要限制，如果mis端调用该方法，则customerId提供一个null即可。
        if (customerId != null) {
        }
        return goodsSnapshotDao.findById(snapshotId);
    }
}