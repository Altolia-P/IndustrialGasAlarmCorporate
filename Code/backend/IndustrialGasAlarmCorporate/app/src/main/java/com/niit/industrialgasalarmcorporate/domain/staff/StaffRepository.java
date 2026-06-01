package com.niit.industrialgasalarmcorporate.domain.staff;

import com.niit.industrialgasalarmcorporate.common.base.Page;

import java.util.Optional;

public interface StaffRepository {

    Optional<Staff> findById(String staffUuid);

    Optional<Staff> findByUserUuid(String userUuid);

    Page<Staff> findAllWithFilter(String name, String role, String status, int page, int size);

    void save(Staff staff);

    void deleteById(String staffUuid);
}
