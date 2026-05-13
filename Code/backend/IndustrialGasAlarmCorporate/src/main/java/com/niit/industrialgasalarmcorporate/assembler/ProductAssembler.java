package com.niit.industrialgasalarmcorporate.assembler;

import com.niit.industrialgasalarmcorporate.application.product.dto.AttributeDTO;
import com.niit.industrialgasalarmcorporate.application.product.dto.CreateProductDTO;
import com.niit.industrialgasalarmcorporate.application.product.dto.ImageDTO;
import com.niit.industrialgasalarmcorporate.application.product.dto.UpdateProductDTO;
import com.niit.industrialgasalarmcorporate.application.product.vo.AttributeVO;
import com.niit.industrialgasalarmcorporate.application.product.vo.ImageVO;
import com.niit.industrialgasalarmcorporate.application.product.vo.ProductDetailVO;
import com.niit.industrialgasalarmcorporate.application.product.vo.ProductVO;
import com.niit.industrialgasalarmcorporate.domain.product.Product;
import com.niit.industrialgasalarmcorporate.domain.product.ProductAttribute;
import com.niit.industrialgasalarmcorporate.domain.product.ProductImage;
import com.niit.industrialgasalarmcorporate.domain.product.ProductStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class ProductAssembler {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private ProductAssembler() {
    }

    public static Product toEntity(CreateProductDTO dto) {
        ProductStatus status = dto.getStatus() != null
                ? ProductStatus.valueOf(dto.getStatus())
                : ProductStatus.PUBLISHED;
        Product product = new Product(
                dto.getName(),
                dto.getDescription(),
                dto.getCoverImage(),
                dto.getCategoryUuid(),
                status
        );
        if (dto.getImages() != null) {
            for (ImageDTO image : dto.getImages()) {
                product.addImage(new ProductImage(image.getUrl(), image.getAltText(), image.getSortOrder()));
            }
        }
        if (dto.getAttributes() != null) {
            for (AttributeDTO attr : dto.getAttributes()) {
                product.addAttribute(new ProductAttribute(attr.getAttrKey(), attr.getAttrVal()));
            }
        }
        return product;
    }

    public static void updateEntity(Product product, UpdateProductDTO dto) {
        if (dto.getName() != null) {
            product.update(dto.getName(),
                    dto.getDescription() != null ? dto.getDescription() : product.getDescription(),
                    dto.getCoverImage() != null ? dto.getCoverImage() : product.getCoverImage(),
                    dto.getCategoryUuid() != null ? dto.getCategoryUuid() : product.getCategoryUuid());
        }
        if (dto.getImages() != null) {
            List<ProductImage> images = dto.getImages().stream()
                    .map(i -> new ProductImage(i.getUrl(), i.getAltText(), i.getSortOrder()))
                    .collect(Collectors.toList());
            product.setImages(images);
        }
        if (dto.getAttributes() != null) {
            List<ProductAttribute> attrs = dto.getAttributes().stream()
                    .map(a -> new ProductAttribute(a.getAttrKey(), a.getAttrVal()))
                    .collect(Collectors.toList());
            product.setAttributes(attrs);
        }
        if (dto.getStatus() != null) {
            product.setStatus(ProductStatus.valueOf(dto.getStatus()));
        }
    }

    public static ProductVO toVO(Product product) {
        ProductVO vo = new ProductVO();
        vo.setProductUuid(product.getProductUuid());
        vo.setName(product.getName());
        vo.setDescription(product.getDescription());
        vo.setCoverImage(product.getCoverImage());
        vo.setCategoryUuid(product.getCategoryUuid());
        vo.setCategoryName(product.getCategoryName());
        vo.setStatus(product.getStatus().name());
        if (product.getCreatedAt() != null) {
            vo.setCreatedAt(product.getCreatedAt().format(FORMATTER));
        }
        return vo;
    }

    public static ProductDetailVO toDetailVO(Product product) {
        ProductDetailVO vo = new ProductDetailVO();
        vo.setProductUuid(product.getProductUuid());
        vo.setName(product.getName());
        vo.setDescription(product.getDescription());
        vo.setCoverImage(product.getCoverImage());
        vo.setImages(product.getImages().stream()
                .map(ProductAssembler::toImageVO)
                .collect(Collectors.toList()));
        vo.setAttributes(product.getAttributes().stream()
                .map(ProductAssembler::toAttributeVO)
                .collect(Collectors.toList()));
        vo.setCategoryUuid(product.getCategoryUuid());
        vo.setCategoryName(product.getCategoryName());
        vo.setStatus(product.getStatus().name());
        if (product.getCreatedAt() != null) {
            vo.setCreatedAt(product.getCreatedAt().format(FORMATTER));
        }
        return vo;
    }

    public static ImageVO toImageVO(ProductImage image) {
        ImageVO vo = new ImageVO();
        vo.setUrl(image.getUrl());
        vo.setAltText(image.getAltText());
        vo.setSortOrder(image.getSortOrder());
        return vo;
    }

    public static AttributeVO toAttributeVO(ProductAttribute attribute) {
        AttributeVO vo = new AttributeVO();
        vo.setAttrKey(attribute.getAttrKey());
        vo.setAttrVal(attribute.getAttrVal());
        return vo;
    }
}
