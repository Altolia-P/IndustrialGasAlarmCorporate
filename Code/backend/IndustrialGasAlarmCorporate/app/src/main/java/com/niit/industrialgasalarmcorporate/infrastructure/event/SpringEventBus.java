package com.niit.industrialgasalarmcorporate.infrastructure.event;

import com.niit.industrialgasalarmcorporate.domain.event.EventBus;
import com.niit.industrialgasalarmcorporate.domain.shared.DomainEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SpringEventBus implements EventBus {

    private final ApplicationEventPublisher publisher;

    @Override
    public void publish(DomainEvent event) {
        log.debug("发布领域事件: {} id={}", event.getClass().getSimpleName(), event.getEventId());
        publisher.publishEvent(event);
    }
}
