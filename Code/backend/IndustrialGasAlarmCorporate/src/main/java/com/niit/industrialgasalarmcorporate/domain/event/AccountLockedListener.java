package com.niit.industrialgasalarmcorporate.domain.event;

public interface AccountLockedListener {

    void handle(AccountLockedEvent event);
}
