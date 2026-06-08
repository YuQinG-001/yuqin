package com.yuqin.meinian.api.mis.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yuqin.meinian.api.common.R;
import com.yuqin.meinian.api.mis.DTO.*;
import com.yuqin.meinian.api.mis.VO.ExamPackageDetailVO;
import com.yuqin.meinian.api.mis.VO.PackageWithRuleVO;
import com.yuqin.meinian.api.mis.converter.MedExamPackageConvertMapper;
import com.yuqin.meinian.api.service.MedExamPackageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Tag(name = "体检套餐", description = "提供Mis端体检套餐管理的核心功能")
@RestController
@Validated
@RequestMapping("/mis/goods")
@RequiredArgsConstructor
public class PackageController {
    private final MedExamPackageService       medExamPackageService;
    private final MedExamPackageConvertMapper medExamPackageConvertMapper;

    @PostMapping("/page")
    @Operation(summary = "package分页", description = "获取package分页信息")
    @SaCheckLogin
    @SaCheckPermission(value = {"ROOT", "GOODS:SELECT"}, mode = SaMode.OR)
    public R<IPage<PackageWithRuleVO>> page(@RequestBody @Valid QueryPackagePageDTO dto) {
        IPage<PackageWithRuleVO> packageWithRuleVO = medExamPackageService.queryPackageWithRulePage(dto);
        return R.ok(packageWithRuleVO);
    }

    @PostMapping("/uploadImage")
    @Operation(summary = "上传图片", description = "将图片文件上传到MinIO对象存储")
    @SaCheckLogin
    @SaCheckPermission(value = {"ROOT", "GOODS:INSERT"}, mode = SaMode.OR)
    public R<String> uploadImage(@RequestParam("file") MultipartFile file) {
        // 实际调用 MinIO 上传服务，返回文件路径
        String fileUrl = medExamPackageService.upLoad(file);
        return R.ok(fileUrl);
    }

    @PostMapping("/uploadExcel")
    @Operation(summary = "上传Excel", description = "将Excel文件上传到MinIO对象存储")
    @SaCheckLogin
    @SaCheckPermission(value = {"ROOT", "GOODS:INSERT"}, mode = SaMode.OR)
    public R<String> uploadExcel(@RequestParam("id")
                                 @Valid
                                 @NotNull(message = "id不能小于1")
                                 @Min(value = 1, message = "id不能小于1")
                                 Integer id,
                                 @RequestParam("file") MultipartFile file) {
        // 实际调用 MinIO 上传服务，返回文件路径
        String fileUrl = medExamPackageService.upLoadExcel(id, file);
        return R.ok(fileUrl);
    }

    @GetMapping("/downloadExcel")
    @Operation(summary = "下载Excel", description = "将MinIO对象Excel文件下载到本地")
    @SaCheckLogin
    @SaCheckPermission(value = {"ROOT", "GOODS:INSERT", "GOODS:SELECT", "GOODS:UPDATE"}, mode = SaMode.OR)
    public void downloadExcel(@RequestParam("id")
                              @Valid
                              @NotNull(message = "id不能小于1")
                              @Min(value = 1, message = "id不能小于1")
                              Integer id, HttpServletResponse response) {
        medExamPackageService.download(id, response);
    }

    @PostMapping("/save")
    @Operation(summary = "保存体检数据", description = "保存体检套餐数据")
    @SaCheckLogin
    @SaCheckPermission(value = {"ROOT", "GOODS:INSERT"}, mode = SaMode.OR)
    public R<String> saveMedExamPackage(@RequestBody @Valid SaveMedExamPackageDTO dto) {
        int i = medExamPackageService.saveMedExamPackage(dto);
        return R.ok("成功保存 " + i + " 条数据。");
    }

    @PostMapping("/findExam")
    @Operation(summary = "查询单条体检数据", description = "查询单条体检数据，用于展示模态窗口")
    @SaCheckLogin
    @SaCheckPermission(value = {"ROOT", "GOODS:SELECT"}, mode = SaMode.OR)
    public R<ExamPackageDetailVO> findExamPackageDetail(@RequestBody @Valid QueryExamPackageDetailDTO dto) {
        ExamPackageDetailVO examPackageDetailVO = medExamPackageService.queryExamPackageDetail(dto);
        return R.ok(examPackageDetailVO);
    }

    @PostMapping("/modify")
    @Operation(summary = "修改体检数据", description = "修改体检套餐页面数据")
    @SaCheckLogin
    @SaCheckPermission(value = {"ROOT", "GOODS:UPDATE"}, mode = SaMode.OR)
    public R<String> modifyMedExamPackage(@RequestBody @Valid ModifyMedExamPackageDTO dto) {
        int i = medExamPackageService.modifyMedExamPackage(dto);
        return R.ok("成功保存 " + i + " 条数据。");
    }

    @PutMapping("/modifyStatus")
    @Operation(summary = "修改上架状态", description = "根据前端传入的指定参数，修改上下架状态")
    @SaCheckLogin
    @SaCheckPermission(value = {"ROOT", "GOODS:UPDATE"}, mode = SaMode.OR)
    public R<String> modifyStatus(@RequestBody @Valid ModifyStatusDTO dto) {
        int i = medExamPackageService.modifyStatus(dto);
        return R.ok("成功修改 " + i + " 条数据。");
    }

    @DeleteMapping("/remove")
    @Operation(summary = "批量删除体检套餐", description = "仅允许删除已下架且销量为0或空的套餐")
    @SaCheckLogin
    @SaCheckPermission(value = {"ROOT", "GOODS:DELETE"}, mode = SaMode.OR)
    public R<String> removeByIds(@RequestParam("ids") @Valid
                                 @NotEmpty(message = "删除内容为空")
                                 List<Integer> ids) {
        int i = medExamPackageService.removeByIds(ids);
        if (i > 0) {
            return R.ok("成功删除 " + i + " 条数据");
        }
        return R.ok("未找到符合条件的记录，已过滤或已删除");
    }
}
