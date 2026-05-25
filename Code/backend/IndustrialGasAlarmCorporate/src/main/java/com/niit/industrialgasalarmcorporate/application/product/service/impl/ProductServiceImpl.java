package com.niit.industrialgasalarmcorporate.application.product.service.impl;

import com.niit.industrialgasalarmcorporate.application.product.dto.CreateProductDTO;
import com.niit.industrialgasalarmcorporate.application.product.dto.UpdateProductDTO;
import com.niit.industrialgasalarmcorporate.application.product.service.ProductService;
import com.niit.industrialgasalarmcorporate.application.product.vo.ProductDetailVO;
import com.niit.industrialgasalarmcorporate.application.product.vo.ProductVO;
import com.niit.industrialgasalarmcorporate.assembler.ProductAssembler;
import com.niit.industrialgasalarmcorporate.common.base.Page;
import com.niit.industrialgasalarmcorporate.common.enums.ErrorCode;
import com.niit.industrialgasalarmcorporate.common.exception.BusinessException;
import com.niit.industrialgasalarmcorporate.common.exception.ProductNotFoundException;
import com.niit.industrialgasalarmcorporate.domain.event.EventBus;
import com.niit.industrialgasalarmcorporate.domain.event.ProductPublishedEvent;
import com.niit.industrialgasalarmcorporate.domain.product.Product;
import com.niit.industrialgasalarmcorporate.domain.product.ProductRepository;
import com.niit.industrialgasalarmcorporate.infrastructure.storage.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final EventBus eventBus;
    private final FileStorageService fileStorageService;

    @Override
    @Transactional
    public ProductVO createProduct(CreateProductDTO dto) {
        Product product = ProductAssembler.toEntity(dto);
        productRepository.save(product);
        return ProductAssembler.toVO(product);
    }

    @Override
    @Transactional
    public ProductVO updateProduct(String productUuid, UpdateProductDTO dto) {
        Product product = productRepository.findById(productUuid)
                .orElseThrow(() -> new ProductNotFoundException(productUuid));
        String oldCoverImage = product.getCoverImage();
        ProductAssembler.updateEntity(product, dto);
        if (dto.getCoverImage() != null && oldCoverImage != null
                && !oldCoverImage.equals(dto.getCoverImage())) {
            fileStorageService.delete(oldCoverImage);
        }
        productRepository.save(product);
        return ProductAssembler.toVO(product);
    }

    @Override
    @Transactional
    public void publishProduct(String productUuid) {
        Product product = productRepository.findById(productUuid)
                .orElseThrow(() -> new ProductNotFoundException(productUuid));
        product.publish();
        productRepository.save(product);
        eventBus.publish(new ProductPublishedEvent(productUuid));
    }

    @Override
    @Transactional
    public void unpublishProduct(String productUuid) {
        Product product = productRepository.findById(productUuid)
                .orElseThrow(() -> new ProductNotFoundException(productUuid));
        product.unpublish();
        productRepository.save(product);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDetailVO getProduct(String productUuid) {
        Product product = productRepository.findById(productUuid)
                .orElseThrow(() -> new ProductNotFoundException(productUuid));
        return ProductAssembler.toDetailVO(product);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductVO> findPublicProducts(String categoryUuid, String name, int page, int size) {
        Page<Product> domainPage = productRepository.findAllWithFilter(name, categoryUuid, "PUBLISHED", page, size);
        return new Page<>(
                domainPage.getContent().stream().map(ProductAssembler::toVO).collect(Collectors.toList()),
                domainPage.getTotalElements(),
                domainPage.getSize(),
                domainPage.getNumber()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductVO> findAdminProducts(String name, String categoryUuid, String status, int page, int size) {
        Page<Product> domainPage = productRepository.findAllWithFilter(name, categoryUuid, status, page, size);
        return new Page<>(
                domainPage.getContent().stream().map(ProductAssembler::toVO).collect(Collectors.toList()),
                domainPage.getTotalElements(),
                domainPage.getSize(),
                domainPage.getNumber()
        );
    }

    @Override
    @Transactional
    public void deleteProduct(String productUuid) {
        Product product = productRepository.findById(productUuid)
                .orElseThrow(() -> new ProductNotFoundException(productUuid));
        if (product.getCoverImage() != null) {
            fileStorageService.delete(product.getCoverImage());
        }
        productRepository.deleteById(productUuid);
    }
}
