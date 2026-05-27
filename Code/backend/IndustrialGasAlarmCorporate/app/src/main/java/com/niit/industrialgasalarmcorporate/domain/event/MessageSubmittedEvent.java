package com.niit.industrialgasalarmcorporate.domain.event;

import com.niit.industrialgasalarmcorporate.domain.shared.DomainEvent;

public class MessageSubmittedEvent extends DomainEvent {

    private final String messageUuid;

    public MessageSubmittedEvent(String messageUuid) {
        super();
        this.messageUuid = messageUuid;
    }

    public String getMessageUuid() {
        return messageUuid;
    }
}
