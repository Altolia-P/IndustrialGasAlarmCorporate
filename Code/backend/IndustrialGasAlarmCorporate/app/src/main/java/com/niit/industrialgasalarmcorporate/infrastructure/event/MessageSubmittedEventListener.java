package com.niit.industrialgasalarmcorporate.infrastructure.event;

import com.niit.industrialgasalarmcorporate.domain.event.MessageSubmittedEvent;
import com.niit.industrialgasalarmcorporate.domain.event.MessageSubmittedListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MessageSubmittedEventListener implements MessageSubmittedListener {

    @Override
    @EventListener
    public void handle(MessageSubmittedEvent event) {
        log.info("收到客户留言: messageUuid={}, eventId={}", event.getMessageUuid(), event.getEventId());
    }
}
