package com.niit.industrialgasalarmcorporate.domain.event;

import com.niit.industrialgasalarmcorporate.domain.shared.DomainEvent;

public interface EventBus {

    void publish(DomainEvent event);
}
