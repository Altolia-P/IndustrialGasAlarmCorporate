package com.niit.industrialgasalarmcorporate.infrastructure.event;

import com.niit.industrialgasalarmcorporate.domain.event.AccountLockedEvent;
import com.niit.industrialgasalarmcorporate.domain.event.AccountLockedListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AccountLockedEventListener implements AccountLockedListener {

    @Override
    @EventListener
    public void handle(AccountLockedEvent event) {
        log.warn("账户已锁定: userUuid={}, eventId={}", event.getUserUuid(), event.getEventId());
    }
}
