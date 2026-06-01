package com.niit.industrialgasalarmcorporate.domain.message;

import com.niit.industrialgasalarmcorporate.common.base.Page;

import java.util.Optional;

public interface MessageRepository {

    void save(ContactMessage message);

    Optional<ContactMessage> findById(String messageUuid);

    Page<ContactMessage> findAll(int page, int size, MessageStatus status);

    Page<ContactMessage> findAllWithFilter(String name, String phone, MessageStatus status, int page, int size);

    Page<ContactMessage> findByAssignedStaffUuid(String staffUuid, int page, int size);

    boolean existsByPhoneInWindow(String phone, int seconds);

    long countByStaffAndStatus(String staffUuid, MessageStatus status);

    long countByStatus(MessageStatus status);
}
