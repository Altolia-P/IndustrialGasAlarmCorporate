package com.niit.industrialgasalarmcorporate.interfaces.admin;

import com.niit.industrialgasalarmcorporate.application.staff.dto.CreateStaffDTO;
import com.niit.industrialgasalarmcorporate.application.staff.dto.UpdateStaffDTO;
import com.niit.industrialgasalarmcorporate.application.staff.service.StaffService;
import com.niit.industrialgasalarmcorporate.application.staff.vo.StaffVO;
import com.niit.industrialgasalarmcorporate.common.base.Page;
import com.niit.industrialgasalarmcorporate.common.base.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminStaffController {

    private final StaffService staffService;

    @GetMapping("/staff")
    public Result<Page<StaffVO>> getStaffs(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return Result.ok(staffService.findStaffs(name, role, status, page, size));
    }

    @GetMapping("/staff/{uuid}")
    public Result<StaffVO> getStaff(@PathVariable String uuid) {
        return Result.ok(staffService.getStaff(uuid));
    }

    @PostMapping("/staff")
    public Result<StaffVO> createStaff(@Valid @RequestBody CreateStaffDTO dto) {
        return Result.ok("创建成功", staffService.createStaff(dto));
    }

    @PutMapping("/staff/{uuid}")
    public Result<StaffVO> updateStaff(@PathVariable String uuid, @Valid @RequestBody UpdateStaffDTO dto) {
        return Result.ok("更新成功", staffService.updateStaff(uuid, dto));
    }

    @DeleteMapping("/staff/{uuid}")
    public Result<Void> deleteStaff(@PathVariable String uuid) {
        staffService.deleteStaff(uuid);
        return Result.ok("删除成功", null);
    }
}
