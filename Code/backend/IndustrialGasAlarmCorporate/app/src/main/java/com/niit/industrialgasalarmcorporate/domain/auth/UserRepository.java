package com.niit.industrialgasalarmcorporate.domain.auth;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserRepository {

    Optional<User> findByUsername(String username);

    Optional<User> findById(String userUuid);

    List<User> findByIds(Collection<String> userUuids);

    Optional<User> findByPhone(String phone);

    void save(User user);
}
