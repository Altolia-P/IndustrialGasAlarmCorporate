package com.niit.industrialgasalarmcorporate.application.product.service;

import com.niit.industrialgasalarmcorporate.application.product.dto.CreateProductDTO;
import com.niit.industrialgasalarmcorporate.application.product.dto.UpdateProductDTO;
import com.niit.industrialgasalarmcorporate.application.product.vo.ProductDetailVO;
import com.niit.industrialgasalarmcorporate.application.product.vo.ProductVO;
import com.niit.industrialgasalarmcorporate.common.base.Page;

public interface ProductService {

    ProductVO createProduct(CreateProductDTO dto);

    ProductVO updateProduct(String productUuid, UpdateProductDTO dto);

    void publishProduct(String productUuid);

    void unpublishProduct(String productUuid);

    ProductDetailVO getProduct(String productUuid);

    Page<ProductVO> findPublicProducts(String categoryUuid, int page, int size);

    Page<ProductVO> findAdminProducts(String name, String categoryUuid, String status, int page, int size);

    void deleteProduct(String productUuid);
}
