package com.yuqin.meinian.api.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import com.yuqin.meinian.api.db.entity.ExaminationDetail;
import com.yuqin.meinian.api.db.entity.GoodsSnapshotEntity;
import com.yuqin.meinian.api.db.entity.MedExamPackageEntity;
import com.yuqin.meinian.api.exception.HisException;
import com.yuqin.meinian.api.front.DTO.MedExamPackagePageQueryDTO;
import com.yuqin.meinian.api.front.VO.ExamPackageDetailForFrontVO;
import com.yuqin.meinian.api.front.VO.MedExamPackagePageQueryVO;
import com.yuqin.meinian.api.front.VO.Top4CustomerFrontVO;
import com.yuqin.meinian.api.mis.DTO.*;
import com.yuqin.meinian.api.mis.VO.ExamPackageDetailVO;
import com.yuqin.meinian.api.mis.VO.PackageWithRuleVO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.TransactionException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * @author YuQin
 * @description 针对表【med_exam_package(体检套餐表)】的数据库操作Service
 * @createDate 2026-04-03 02:27:34
 */
public interface MedExamPackageService extends MPJBaseService<MedExamPackageEntity> {

    /**
     * 分页查询带有促销规则信息的体检套餐列表。
     *
     * <p>该方法通过关联体检套餐表（MedExamPackage）与促销规则表（PromotionRule），
     * 获取套餐的基本信息及其关联的促销规则名称，并额外计算是否存在检查项（hasCheckup）。
     * 支持按套餐名称、套餐编码、套餐类型、分类ID、状态进行动态过滤，结果按套餐ID升序排序。
     *
     * @param dto 分页查询参数，包括页码、每页大小以及各类过滤条件（套餐名称、编码、类型、分类ID、状态等）
     * @return 分页对象 {@link IPage}&lt;{@link PackageWithRuleVO}&gt;，包含符合条件的套餐及规则信息的分页数据
     */
    IPage<PackageWithRuleVO> queryPackageWithRulePage(QueryPackagePageDTO dto);

    /**
     * 上传图片文件到 MinIO 对象存储，并返回文件的访问路径。
     *
     * <p>该方法会使用 UUID 生成唯一文件名，保留原文件扩展名（目前固定为 .jpg），并存储到配置的存储桶中。
     * 上传成功后返回完整的 HTTP 访问路径，方便前端或服务间直接使用。
     *
     * @param file 待上传的文件，不能为 {@code null}，通常为图片文件（如 JPG）
     * @return 文件在 MinIO 中的完整访问路径（例如：/front/goods/img/{uuid}.jpg）
     * @throws HisException 如果文件为空、不符合格式要求或上传失败时抛出
     */
    String upLoad(MultipartFile file);

    /**
     * 保存新的体检套餐。
     *
     * <p>在保存前会自动计算并设置 {@code md5Hash} 数据校验值，用于后续防篡改或乐观锁校验。
     * <p>该方法会使用 MapStruct 转换器将 DTO 转换为实体，保存时忽略实体中由数据库自动处理的字段
     * （如 id、createTime、updateTime 等）。MD5 摘要会排除这些自动字段，仅对业务数据进行签名。
     *
     * @param dto 待保存的体检套餐入参（应包含套餐编码、名称、检查项等必要业务数据）
     * @return 数据库受影响的行数（通常为1表示保存成功）
     * @throws DuplicateKeyException 如果套餐编码等唯一约束冲突
     * @throws TransactionException  事务回滚时抛出
     * @throws HisException          当 MD5 生成或数据库操作失败时抛出
     * @implNote 该方法标记为 {@code @Transactional}，任何运行时异常都会触发事务回滚
     */
    int saveMedExamPackage(SaveMedExamPackageDTO dto);

    /**
     * 查询体检套餐详情（管理端使用，可查询任意状态）。
     *
     * <p>通过套餐 ID 查询完整的套餐信息，包括检查项列表（examItems）、促销规则等。
     * 该方法不限制套餐的上下架状态，供管理后台编辑、预览时使用。
     *
     * @param dto 包含套餐 ID 的查询参数
     * @return 套餐详情视图对象 {@link ExamPackageDetailVO}，如果套餐不存在则返回 {@code null}
     */
    ExamPackageDetailVO queryExamPackageDetail(QueryExamPackageDetailDTO dto);

    /**
     * 查询体检套餐详情（前端展示使用，仅返回上架状态的套餐）。
     *
     * <p>通过套餐 ID 查询完整的套餐信息，但会自动过滤状态为“下架”（status != 1）的套餐，
     * 若套餐已下架或不存在则返回 {@code null}。适用于 C 端商品详情页展示。
     *
     * @param id 套餐 ID
     * @return 套餐详情视图对象 {@link ExamPackageDetailVO}，如果套餐不存在或已下架则返回 {@code null}
     */
    ExamPackageDetailForFrontVO queryExamPackageDetailForFront(int id);

    /**
     * 修改体检套餐信息。
     *
     * <p>支持更新套餐的基本信息、价格、促销规则、封面图等。更新前会自动重新计算 {@code md5Hash}，
     * 确保数据完整性。该方法使用 MapStruct 将 DTO 转换为实体，然后通过 MyBatis-Plus 根据 ID 更新。
     *
     * @param dto 包含套餐 ID 及待更新字段的 DTO
     * @return 数据库受影响的行数（通常为1表示修改成功）
     * @throws HisException 当套餐不存在或 MD5 计算失败时抛出
     */
    int modifyMedExamPackage(ModifyMedExamPackageDTO dto);

    /**
     * 上传并解析 Excel 文件，将体检项目批量导入到指定套餐的 examItems 字段。
     *
     * <p>Excel 文件第一行为标题行（列顺序：地点、名称、项目、类型、代码、性别、值、模板），
     * 第二行开始为数据。该方法会读取所有数据行，转换为 {@link ExaminationDetail} 列表，
     * 然后更新对应套餐的 {@code examItems} 字段（JSON 类型），并同步更新 {@code md5Hash}。
     *
     * @param id   套餐 ID
     * @param file 要上传的 Excel 文件（.xlsx 格式）
     * @return 文件在 MinIO 中存储的路径（例如：/front/goods/excel/{id}.xlsx）
     * @throws HisException 如果文件为空、解析失败、或套餐不存在时抛出
     */
    String upLoadExcel(Integer id, MultipartFile file);

    /**
     * 下载指定套餐关联的 Excel 体检项目文件。
     *
     * <p>根据套餐 ID 在 MinIO 中查找对应的 Excel 文件（路径固定为 /front/goods/excel/{id}.xlsx），
     * 若文件存在则设置响应头（Content-Disposition 为附件下载），并将文件内容写入输出流。
     *
     * @param id       套餐 ID
     * @param response HttpServletResponse 对象，用于输出文件流
     * @throws HisException 如果文件不存在或 IO 读写失败时抛出
     */
    void download(int id, HttpServletResponse response);

    /**
     * 修改套餐的上下架状态。
     *
     * <p>仅更新 {@code status} 字段（0-下架，1-上架）。如果更新行数为 0（例如 ID 不存在），则抛出异常。
     *
     * @param dto 包含套餐 ID 和目标状态的 DTO
     * @return 数据库受影响的行数（成功时返回 1）
     * @throws HisException 当未找到对应套餐或更新失败时抛出
     */
    int modifyStatus(ModifyStatusDTO dto);

    /**
     * 批量查询套餐的封面图片路径。
     *
     * <p>仅返回满足“已下架（status=0）且销量为 0 或为空”的套餐的封面图片路径，
     * 用于在删除套餐前获取需要从 MinIO 中清理的图片文件列表。
     *
     * @param ids 套餐 ID 列表
     * @return 去重后的封面图片路径列表，不包含 {@code null} 值
     */
    List<String> queryCoverImage(List<Integer> ids);

    /**
     * 批量删除套餐（仅允许删除已下架且销量为 0 的套餐）。
     *
     * <p>先执行数据库删除操作（条件：status=0 且 (salesVolume=0 or salesVolume is null)，
     * 且 ID 在给定列表中）。删除成功后，再异步清理对应套餐在 MinIO 中的封面图片文件。
     * 注意：MinIO 删除失败不会回滚数据库事务，仅记录错误日志，需要人工介入清理。
     *
     * @param ids 要删除的套餐 ID 列表
     * @return 实际删除的数据库记录数
     * @throws HisException 当数据库删除行数为 0（没有符合条件的套餐）时抛出
     */
    int removeByIds(List<Integer> ids);

    Map<Integer, List<Top4CustomerFrontVO>> findTop4ByCategoryIdOrderBySalesDesc(Integer[] categoryIds);

    IPage<MedExamPackagePageQueryVO> pageQueryByCondition(MedExamPackagePageQueryDTO dto);

    GoodsSnapshotEntity findPackageAndPromotionById(Integer id);

    /**
     * 当front端使用时，根据商品快照id和客户id获取商品信息
     * 当mis端使用时，根据商品快照id获取商品信息
     * @param snapshotId 商品快照id
     * @param customerId 客户id
     * @return 商品信息
     */
    GoodsSnapshotEntity findBySnapshotId(String snapshotId, Integer customerId);
}
