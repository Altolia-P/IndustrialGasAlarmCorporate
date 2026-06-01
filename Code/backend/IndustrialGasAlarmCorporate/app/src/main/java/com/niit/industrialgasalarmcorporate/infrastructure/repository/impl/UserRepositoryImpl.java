package com.niit.industrialgasalarmcorporate.infrastructure.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.niit.industrialgasalarmcorporate.domain.auth.User;
import com.niit.industrialgasalarmcorporate.domain.auth.UserRepository;
import com.niit.industrialgasalarmcorporate.infrastructure.repository.mapper.UserMapper;
import com.niit.industrialgasalarmcorporate.infrastructure.repository.po.UserPO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserMapper userMapper;

    @Override
    public Optional<User> findByUsername(String username) {
        LambdaQueryWrapper<UserPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserPO::getUsername, username);
        UserPO po = userMapper.selectOne(wrapper);
        if (po == null) {
            return Optional.empty();
        }
        return Optional.of(toDomain(po));
    }

    @Override
    public List<User> findByIds(Collection<String> userUuids) {
        if (userUuids == null || userUuids.isEmpty()) return Collections.emptyList();
        return userMapper.selectBatchIds(userUuids).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<User> findByPhone(String phone) {
        LambdaQueryWrapper<UserPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserPO::getPhone, phone);
        UserPO po = userMapper.selectOne(wrapper);
        if (po == null) {
            return Optional.empty();
        }
        return Optional.of(toDomain(po));
    }

    @Override
    public Optional<User> findById(String userUuid) {
        UserPO po = userMapper.selectById(userUuid);
        if (po == null) {
            return Optional.empty();
        }
        return Optional.of(toDomain(po));
    }

    @Override
    public void save(User user) {
        UserPO po = toPO(user);
        UserPO existing = userMapper.selectById(user.getUserUuid());
        if (existing != null) {
            userMapper.updateById(po);
        } else {
            userMapper.insert(po);
        }
    }

    private User toDomain(UserPO po) {
        return new User(
                po.getUserUuid(),
                po.getUsername(),
                po.getPasswordHash(),
                po.getPhone(),
                po.getCompany(),
                po.getFailCount(),
                po.getLocked() != null && po.getLocked() == 1,
                po.getLockTime(),
                po.getLastLoginAt(),
                po.getRole()
        );
    }

    private UserPO toPO(User user) {
        UserPO po = new UserPO();
        po.setUserUuid(user.getUserUuid());
        po.setUsername(user.getUsername());
        po.setPasswordHash(user.getPasswordHash());
        po.setPhone(user.getPhone());
        po.setCompany(user.getCompany());
        po.setFailCount(user.getFailCount());
        po.setLocked(user.getLocked() ? 1 : 0);
        po.setLockTime(user.getLockTime());
        po.setLastLoginAt(user.getLastLoginAt());
        po.setRole(user.getRole());
        return po;
    }
}
