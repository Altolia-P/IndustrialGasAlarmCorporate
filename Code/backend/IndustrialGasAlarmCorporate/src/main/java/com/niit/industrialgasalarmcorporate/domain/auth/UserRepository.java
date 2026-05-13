package com.niit.industrialgasalarmcorporate.domain.auth;

import java.util.Optional;

public interface UserRepository {

    Optional<User> findByUsername(String username);

    Optional<User> findById(String userUuid);

    void save(User user);
}
