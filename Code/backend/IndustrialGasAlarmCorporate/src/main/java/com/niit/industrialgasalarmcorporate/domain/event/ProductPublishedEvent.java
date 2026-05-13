package com.niit.industrialgasalarmcorporate.domain.event;

import com.niit.industrialgasalarmcorporate.domain.shared.DomainEvent;

public class ProductPublishedEvent extends DomainEvent {

    private final String productUuid;

    public ProductPublishedEvent(String productUuid) {
        super();
        this.productUuid = productUuid;
    }

    public String getProductUuid() {
        return productUuid;
    }
}
