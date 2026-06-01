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
import com.niit.industrialgasalarmcorporate.infrastructure.storage.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminProductController {

    private final ProductService productService;
    private final FileStorageService fileStorageService;
    private final ObjectMapper objectMapper;

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
                // ignore malformed JSON, attributes are optional
            }
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
                // ignore
            }
        }
        return Result.ok("修改成功", productService.updateProduct(uuid, dto));
    }

    @DeleteMapping("/products/{uuid}")
    public Result<Void> deleteProduct(@PathVariable String uuid) {
        productService.deleteProduct(uuid);
        return Result.ok("删除成功", null);
    }

    @PostMapping("/products/{uuid}/publish")
    public Result<Void> publishProduct(@PathVariable String uuid) {
        productService.publishProduct(uuid);
        return Result.ok("上架成功", null);
    }

    @PostMapping("/products/{uuid}/unpublish")
    public Result<Void> unpublishProduct(@PathVariable String uuid) {
        productService.unpublishProduct(uuid);
        return Result.ok("下架成功", null);
    }
}
