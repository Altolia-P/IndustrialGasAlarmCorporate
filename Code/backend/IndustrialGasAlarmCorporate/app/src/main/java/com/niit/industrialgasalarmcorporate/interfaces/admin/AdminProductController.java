package com.niit.industrialgasalarmcorporate.interfaces.admin;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.niit.industrialgasalarmcorporate.application.product.dto.AttributeDTO;
import com.niit.industrialgasalarmcorporate.application.product.dto.CreateProductDTO;
import com.niit.industrialgasalarmcorporate.application.product.dto.ImageDTO;
import com.niit.industrialgasalarmcorporate.application.product.dto.UpdateProductDTO;
import com.niit.industrialgasalarmcorporate.application.product.service.ProductService;
import com.niit.industrialgasalarmcorporate.application.product.vo.ProductDetailVO;
import com.niit.industrialgasalarmcorporate.application.product.vo.ProductVO;
import com.niit.industrialgasalarmcorporate.common.base.Page;
import com.niit.industrialgasalarmcorporate.common.base.Result;
import com.niit.industrialgasalarmcorporate.common.enums.ErrorCode;
import com.niit.industrialgasalarmcorporate.common.exception.BusinessException;
import com.niit.industrialgasalarmcorporate.infrastructure.aop.LogOperation;
import com.niit.industrialgasalarmcorporate.infrastructure.storage.FileStorageService;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminProductController {

    private final ProductService productService;
    private final FileStorageService fileStorageService;
    private final ObjectMapper objectMapper;
    private final Validator validator;

    @LogOperation(operation = "CREATE", targetType = "PRODUCT")
    @PostMapping(value = "/products", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<ProductVO> createProduct(
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam String categoryUuid,
            @RequestParam(required = false) MultipartFile coverImage,
            @RequestParam(required = false) List<MultipartFile> images,
            @RequestParam(required = false) String attributesJson,
            @RequestParam(required = false) String status) {
        CreateProductDTO dto = new CreateProductDTO();
        dto.setName(name);
        dto.setDescription(description);
        dto.setCategoryUuid(categoryUuid);
        dto.setStatus(status);
        // Parse JSON and validate BEFORE uploading files — avoids orphan files on error
        if (attributesJson != null && !attributesJson.isBlank()) {
            try {
                List<AttributeDTO> attrs = objectMapper.readValue(
                        attributesJson, new TypeReference<List<AttributeDTO>>() {});
                dto.setAttributes(attrs);
            } catch (Exception e) {
                log.warn("解析产品属性JSON失败: {}", e.getMessage());
                throw new BusinessException(ErrorCode.VALIDATION_ERROR, "属性数据JSON格式错误: " + e.getMessage());
            }
        }
        var violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            String msg = violations.stream()
                    .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                    .collect(Collectors.joining(", "));
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, msg);
        }
        if (coverImage != null && !coverImage.isEmpty()) {
            dto.setCoverImage(fileStorageService.store(coverImage));
        }
        if (images != null && !images.isEmpty()) {
            List<ImageDTO> imageList = new ArrayList<>();
            for (int i = 0; i < images.size(); i++) {
                MultipartFile img = images.get(i);
                if (!img.isEmpty()) {
                    String url = fileStorageService.store(img);
                    ImageDTO imageDTO = new ImageDTO();
                    imageDTO.setUrl(url);
                    imageDTO.setAltText(img.getOriginalFilename());
                    imageDTO.setSortOrder(i);
                    imageList.add(imageDTO);
                }
            }
            dto.setImages(imageList);
        }
        return Result.ok("新增成功", productService.createProduct(dto));
    }

    @GetMapping("/products/{uuid}")
    public Result<ProductDetailVO> getProduct(@PathVariable String uuid) {
        return Result.ok(productService.getProduct(uuid));
    }

    @GetMapping("/products")
    public Result<Page<ProductVO>> getProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String categoryUuid,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return Result.ok(productService.findAdminProducts(name, categoryUuid, status, page, size));
    }

    @LogOperation(operation = "UPDATE", targetType = "PRODUCT")
    @PutMapping(value = "/products/{uuid}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<ProductVO> updateProduct(
            @PathVariable String uuid,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String categoryUuid,
            @RequestParam(required = false) MultipartFile coverImage,
            @RequestParam(required = false) List<MultipartFile> images,
            @RequestParam(required = false) String attributesJson,
            @RequestParam(required = false) String status) {
        UpdateProductDTO dto = new UpdateProductDTO();
        if (name != null) dto.setName(name);
        if (description != null) dto.setDescription(description);
        if (categoryUuid != null) dto.setCategoryUuid(categoryUuid);
        if (status != null) dto.setStatus(status);
        if (coverImage != null && !coverImage.isEmpty()) {
            dto.setCoverImage(fileStorageService.store(coverImage));
        }
        if (images != null && !images.isEmpty()) {
            List<ImageDTO> imageList = new ArrayList<>();
            for (int i = 0; i < images.size(); i++) {
                MultipartFile img = images.get(i);
                if (!img.isEmpty()) {
                    String url = fileStorageService.store(img);
                    ImageDTO imageDTO = new ImageDTO();
                    imageDTO.setUrl(url);
                    imageDTO.setAltText(img.getOriginalFilename());
                    imageDTO.setSortOrder(i);
                    imageList.add(imageDTO);
                }
            }
            dto.setImages(imageList);
        }
        if (attributesJson != null && !attributesJson.isBlank()) {
            try {
                List<AttributeDTO> attrs = objectMapper.readValue(
                        attributesJson, new TypeReference<List<AttributeDTO>>() {});
                dto.setAttributes(attrs);
            } catch (Exception e) {
                log.warn("解析产品属性JSON失败: {}", e.getMessage());
                throw new BusinessException(ErrorCode.VALIDATION_ERROR, "属性数据JSON格式错误: " + e.getMessage());
            }
        }
        var violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            String msg = violations.stream()
                    .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                    .collect(Collectors.joining(", "));
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, msg);
        }
        return Result.ok("修改成功", productService.updateProduct(uuid, dto));
    }

    @LogOperation(operation = "DELETE", targetType = "PRODUCT")
    @DeleteMapping("/products/{uuid}")
    public Result<Void> deleteProduct(@PathVariable String uuid) {
        productService.deleteProduct(uuid);
        return Result.ok("删除成功", null);
    }

    @LogOperation(operation = "PUBLISH", targetType = "PRODUCT")
    @PutMapping("/products/{uuid}/publish")
    public Result<Void> publishProduct(@PathVariable String uuid) {
        productService.publishProduct(uuid);
        return Result.ok("上架成功", null);
    }

    @LogOperation(operation = "UNPUBLISH", targetType = "PRODUCT")
    @PutMapping("/products/{uuid}/unpublish")
    public Result<Void> unpublishProduct(@PathVariable String uuid) {
        productService.unpublishProduct(uuid);
        return Result.ok("下架成功", null);
    }
}
