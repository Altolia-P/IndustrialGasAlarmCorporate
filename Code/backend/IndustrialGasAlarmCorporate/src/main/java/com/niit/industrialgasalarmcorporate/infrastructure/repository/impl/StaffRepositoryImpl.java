package com.niit.industrialgasalarmcorporate.infrastructure.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niit.industrialgasalarmcorporate.domain.staff.Staff;
import com.niit.industrialgasalarmcorporate.domain.staff.StaffRepository;
import com.niit.industrialgasalarmcorporate.domain.staff.StaffRole;
import com.niit.industrialgasalarmcorporate.domain.staff.StaffStatus;
import com.niit.industrialgasalarmcorporate.infrastructure.repository.mapper.StaffMapper;
import com.niit.industrialgasalarmcorporate.infrastructure.repository.po.StaffPO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class StaffRepositoryImpl implements StaffRepository {

    private final StaffMapper staffMapper;

    @Override
    public Optional<Staff> findById(String staffUuid) {
        StaffPO po = staffMapper.selectById(staffUuid);
        if (po == null) {
            return Optional.empty();
        }
        return Optional.of(toDomain(po));
    }

    @Override
    public com.niit.industrialgasalarmcorporate.common.base.Page<Staff> findAllWithFilter(
            String name, String role, String status, int page, int size) {
        LambdaQueryWrapper<StaffPO> wrapper = new LambdaQueryWrapper<>();
        if (name != null && !name.isBlank()) {
            wrapper.like(StaffPO::getName, name);
        }
        if (role != null && !role.isBlank()) {
            wrapper.eq(StaffPO::getRole, role);
        }
        if (status != null && !status.isBlank()) {
            wrapper.eq(StaffPO::getStatus, status);
        }
        wrapper.orderByDesc(StaffPO::getCreatedAt);
        Page<StaffPO> mpPage = new Page<>(page, size);
        Page<StaffPO> result = staffMapper.selectPage(mpPage, wrapper);
        List<Staff> staffList = result.getRecords().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
        return new com.niit.industrialgasalarmcorporate.common.base.Page<>(
                staffList, result.getTotal(), (int) result.getSize(), (int) result.getCurrent());
    }

    @Override
    public void save(Staff staff) {
        StaffPO po = toPO(staff);
        StaffPO existing = staffMapper.selectById(staff.getStaffUuid());
        if (existing != null) {
            staffMapper.updateById(po);
        } else {
            staffMapper.insert(po);
        }
    }

    @Override
    public void deleteById(String staffUuid) {
        staffMapper.deleteById(staffUuid);
    }

    private Staff toDomain(StaffPO po) {
        return new Staff(
                po.getStaffUuid(),
                po.getName(),
                po.getPhone(),
                po.getEmail(),
                StaffRole.valueOf(po.getRole()),
                StaffStatus.valueOf(po.getStatus()),
                po.getCreatedAt()
        );
    }

    private StaffPO toPO(Staff staff) {
        StaffPO po = new StaffPO();
        po.setStaffUuid(staff.getStaffUuid());
        po.setName(staff.getName());
        po.setPhone(staff.getPhone());
        po.setEmail(staff.getEmail());
        po.setRole(staff.getRole().name());
        po.setStatus(staff.getStatus().name());
        return po;
    }
}
