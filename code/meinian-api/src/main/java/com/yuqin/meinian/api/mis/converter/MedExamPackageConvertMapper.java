package com.yuqin.meinian.api.mis.converter;

import com.yuqin.meinian.api.db.entity.ExamItem;
import com.yuqin.meinian.api.db.entity.MedExamPackageEntity;
import com.yuqin.meinian.api.mis.DTO.ModifyMedExamPackageDTO;
import com.yuqin.meinian.api.mis.DTO.SaveMedExamPackageDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.Arrays;
import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MedExamPackageConvertMapper {
    @Mapping(target = "salesVolume", constant = "0")
    @Mapping(target = "status", constant = "0")
    @Mapping(target = "tags", source = "tags", qualifiedByName = "toList")
    @Mapping(target = "departmentExam", qualifiedByName = "nullIfEmptyExam")
    @Mapping(target = "labExam", qualifiedByName = "nullIfEmptyExam")
    @Mapping(target = "medicalExam", qualifiedByName = "nullIfEmptyExam")
    @Mapping(target = "otherExam", qualifiedByName = "nullIfEmptyExam")
    MedExamPackageEntity toEntity(ModifyMedExamPackageDTO dto);

    @Mapping(target = "salesVolume", constant = "0")
    @Mapping(target = "status", constant = "0")
    @Mapping(target = "tags", source = "tags", qualifiedByName = "toList")
    @Mapping(target = "departmentExam", qualifiedByName = "nullIfEmptyExam")
    @Mapping(target = "labExam", qualifiedByName = "nullIfEmptyExam")
    @Mapping(target = "medicalExam", qualifiedByName = "nullIfEmptyExam")
    @Mapping(target = "otherExam", qualifiedByName = "nullIfEmptyExam")
    MedExamPackageEntity toEntity(SaveMedExamPackageDTO dto);

    @Named("toList")
    default List<String> convertArrayToList(String[] tags) {
        return tags == null ? null : Arrays.asList(tags);
    }

    @Named("nullIfEmptyExam")
    default List<ExamItem> nullIfEmpty(List<ExamItem> list) {
        return (list == null || list.isEmpty()) ? null : list;
    }
}
