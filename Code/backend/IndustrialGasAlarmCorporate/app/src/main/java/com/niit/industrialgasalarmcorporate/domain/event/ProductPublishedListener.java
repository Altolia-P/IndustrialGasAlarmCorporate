package com.niit.industrialgasalarmcorporate.domain.event;

public interface ProductPublishedListener {

    void handle(ProductPublishedEvent event);
}
