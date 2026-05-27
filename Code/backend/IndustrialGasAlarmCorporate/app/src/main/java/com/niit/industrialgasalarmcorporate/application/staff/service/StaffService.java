package com.niit.industrialgasalarmcorporate.application.staff.service;

import com.niit.industrialgasalarmcorporate.application.staff.dto.CreateStaffDTO;
import com.niit.industrialgasalarmcorporate.application.staff.dto.UpdateStaffDTO;
import com.niit.industrialgasalarmcorporate.application.staff.vo.StaffVO;
import com.niit.industrialgasalarmcorporate.common.base.Page;

public interface StaffService {

    Page<StaffVO> findStaffs(String name, String role, String status, int page, int size);

    StaffVO getStaff(String staffUuid);

    StaffVO createStaff(CreateStaffDTO dto);

    StaffVO updateStaff(String staffUuid, UpdateStaffDTO dto);

    void deleteStaff(String staffUuid);
}
