package com.niit.industrialgasalarmcorporate.assembler;

import com.niit.industrialgasalarmcorporate.application.staff.dto.CreateStaffDTO;
import com.niit.industrialgasalarmcorporate.application.staff.dto.UpdateStaffDTO;
import com.niit.industrialgasalarmcorporate.application.staff.vo.StaffVO;
import com.niit.industrialgasalarmcorporate.domain.staff.Staff;
import com.niit.industrialgasalarmcorporate.domain.staff.StaffRole;
import com.niit.industrialgasalarmcorporate.domain.staff.StaffStatus;

import java.time.format.DateTimeFormatter;

public final class StaffAssembler {

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private StaffAssembler() {}

    public static Staff toEntity(CreateStaffDTO dto) {
        return new Staff(
                dto.getName(),
                dto.getPhone(),
                dto.getEmail(),
                StaffRole.valueOf(dto.getRole()),
                StaffStatus.valueOf(dto.getStatus())
        );
    }

    public static void updateEntity(Staff staff, UpdateStaffDTO dto) {
        staff.update(
                dto.getName(),
                dto.getPhone(),
                dto.getEmail(),
                dto.getRole() != null ? StaffRole.valueOf(dto.getRole()) : null,
                dto.getStatus() != null ? StaffStatus.valueOf(dto.getStatus()) : null
        );
    }

    public static StaffVO toVO(Staff staff) {
        StaffVO vo = new StaffVO();
        vo.setStaffUuid(staff.getStaffUuid());
        vo.setName(staff.getName());
        vo.setPhone(staff.getPhone());
        vo.setEmail(staff.getEmail());
        vo.setRole(staff.getRole().name());
        vo.setStatus(staff.getStatus().name());
        vo.setCreatedAt(staff.getCreatedAt().format(DTF));
        return vo;
    }
}
