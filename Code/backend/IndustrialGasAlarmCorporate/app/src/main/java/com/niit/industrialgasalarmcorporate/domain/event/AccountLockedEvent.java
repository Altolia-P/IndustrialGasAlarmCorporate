package com.niit.industrialgasalarmcorporate.domain.event;

import com.niit.industrialgasalarmcorporate.domain.shared.DomainEvent;

public class AccountLockedEvent extends DomainEvent {

    private final String userUuid;

    public AccountLockedEvent(String userUuid) {
        super();
        this.userUuid = userUuid;
    }

    public String getUserUuid() {
        return userUuid;
    }
}
