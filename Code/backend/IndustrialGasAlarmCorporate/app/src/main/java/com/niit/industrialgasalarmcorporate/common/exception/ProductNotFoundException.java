package com.niit.industrialgasalarmcorporate.common.exception;

import com.niit.industrialgasalarmcorporate.common.enums.ErrorCode;

public class ProductNotFoundException extends BusinessException {

    private final String productUuid;

    public ProductNotFoundException(String productUuid) {
        super(ErrorCode.PRODUCT_NOT_FOUND);
        this.productUuid = productUuid;
    }

    public String getProductUuid() {
        return productUuid;
    }
}
