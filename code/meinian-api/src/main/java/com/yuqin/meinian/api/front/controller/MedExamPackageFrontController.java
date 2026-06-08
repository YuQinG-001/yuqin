package com.yuqin.meinian.api.front.controller;

import com.yuqin.meinian.api.common.R;
import com.yuqin.meinian.api.front.DTO.FindTop4ByCategoryIdsDTO;
import com.yuqin.meinian.api.front.VO.Top4CustomerFrontVO;
import com.yuqin.meinian.api.service.MedExamPackageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@Validated
@RequestMapping()
@RequiredArgsConstructor
public class MedExamPackageFrontController {


}
