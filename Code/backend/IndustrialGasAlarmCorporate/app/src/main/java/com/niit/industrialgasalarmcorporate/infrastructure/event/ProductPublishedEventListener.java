package com.niit.industrialgasalarmcorporate.infrastructure.event;

import com.niit.industrialgasalarmcorporate.domain.event.ProductPublishedEvent;
import com.niit.industrialgasalarmcorporate.domain.event.ProductPublishedListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ProductPublishedEventListener implements ProductPublishedListener {

    @Override
    @EventListener
    public void handle(ProductPublishedEvent event) {
        log.info("产品已上架: productUuid={}, eventId={}", event.getProductUuid(), event.getEventId());
    }
}
