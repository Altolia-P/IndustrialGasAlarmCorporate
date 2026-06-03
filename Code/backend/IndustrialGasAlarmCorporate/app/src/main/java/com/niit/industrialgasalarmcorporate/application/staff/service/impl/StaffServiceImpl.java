package com.niit.industrialgasalarmcorporate.application.staff.service.impl;

import com.niit.industrialgasalarmcorporate.application.staff.dto.CreateStaffDTO;
import com.niit.industrialgasalarmcorporate.application.staff.dto.UpdateStaffDTO;
import com.niit.industrialgasalarmcorporate.application.staff.dto.UpdateStaffProfileDTO;
import com.niit.industrialgasalarmcorporate.application.staff.service.StaffService;
import com.niit.industrialgasalarmcorporate.application.staff.vo.StaffVO;
import com.niit.industrialgasalarmcorporate.assembler.StaffAssembler;
import com.niit.industrialgasalarmcorporate.common.base.Page;
import com.niit.industrialgasalarmcorporate.common.enums.ErrorCode;
import com.niit.industrialgasalarmcorporate.common.exception.BusinessException;
import com.niit.industrialgasalarmcorporate.domain.auth.PasswordHasher;
import com.niit.industrialgasalarmcorporate.domain.auth.User;
import com.niit.industrialgasalarmcorporate.domain.auth.UserRepository;
import com.niit.industrialgasalarmcorporate.domain.staff.Staff;
import com.niit.industrialgasalarmcorporate.domain.staff.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StaffServiceImpl implements StaffService {

    private final StaffRepository staffRepository;
    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;

    @Override
    @Transactional(readOnly = true)
    public Page<StaffVO> findStaffs(String name, String role, String status, int page, int size) {
        Page<Staff> domainPage = staffRepository.findAllWithFilter(name, role, status, page, size);
        return new Page<>(
                domainPage.getContent().stream().map(StaffAssembler::toVO).collect(Collectors.toList()),
                domainPage.getTotalElements(),
                domainPage.getSize(),
                domainPage.getNumber()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public StaffVO getStaff(String staffUuid) {
        Staff staff = staffRepository.findById(staffUuid)
                .orElseThrow(() -> new BusinessException(ErrorCode.STAFF_NOT_FOUND));
        return StaffAssembler.toVO(staff);
    }

    @Override
    @Transactional
    public StaffVO createStaff(CreateStaffDTO dto) {
        userRepository.findByUsername(dto.getUsername()).ifPresent(u -> {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "账号已存在");
        });
        Staff staff = StaffAssembler.toEntity(dto);
        User user = new User(dto.getUsername(), passwordHasher.hash(dto.getPassword()),
                dto.getPhone(), null, "STAFF");
        userRepository.save(user);
        staff.setUserUuid(user.getUserUuid());
        staffRepository.save(staff);
        return StaffAssembler.toVO(staff);
    }

    @Override
    @Transactional
    public StaffVO updateStaff(String staffUuid, UpdateStaffDTO dto) {
        Staff staff = staffRepository.findById(staffUuid)
                .orElseThrow(() -> new BusinessException(ErrorCode.STAFF_NOT_FOUND));
        StaffAssembler.updateEntity(staff, dto);
        staffRepository.save(staff);
        return StaffAssembler.toVO(staff);
    }

    @Override
    @Transactional
    public void deleteStaff(String staffUuid) {
        Staff staff = staffRepository.findById(staffUuid)
                .orElseThrow(() -> new BusinessException(ErrorCode.STAFF_NOT_FOUND));
        if (staff.getUserUuid() != null) {
            userRepository.findById(staff.getUserUuid()).ifPresent(u -> {
                userRepository.delete(u.getUserUuid());
            });
        }
        staffRepository.deleteById(staffUuid);
    }

    @Override
    @Transactional(readOnly = true)
    public StaffVO getMyProfile(String userUuid) {
        Staff staff = staffRepository.findByUserUuid(userUuid)
                .orElseThrow(() -> new BusinessException(ErrorCode.STAFF_NOT_FOUND));
        return StaffAssembler.toVO(staff);
    }

    @Override
    @Transactional
    public StaffVO updateMyProfile(String userUuid, UpdateStaffProfileDTO dto) {
        Staff staff = staffRepository.findByUserUuid(userUuid)
                .orElseThrow(() -> new BusinessException(ErrorCode.STAFF_NOT_FOUND));
        staff.update(dto.getName(), dto.getPhone(), dto.getEmail(), null, null);
        staffRepository.save(staff);
        return StaffAssembler.toVO(staff);
    }
}
