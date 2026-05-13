package com.niit.industrialgasalarmcorporate.domain.event;

public interface MessageSubmittedListener {

    void handle(MessageSubmittedEvent event);
}
