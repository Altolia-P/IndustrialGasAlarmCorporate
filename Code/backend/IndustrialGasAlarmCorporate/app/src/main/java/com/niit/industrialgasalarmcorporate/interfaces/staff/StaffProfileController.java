package com.niit.industrialgasalarmcorporate.interfaces.staff;

import com.niit.industrialgasalarmcorporate.application.staff.dto.UpdateStaffProfileDTO;
import com.niit.industrialgasalarmcorporate.application.staff.service.StaffService;
import com.niit.industrialgasalarmcorporate.application.staff.vo.StaffVO;
import com.niit.industrialgasalarmcorporate.common.base.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/staff")
@RequiredArgsConstructor
public class StaffProfileController {

    private final StaffService staffService;

    @GetMapping("/me")
    public Result<StaffVO> getMyProfile(@RequestAttribute String userUuid) {
        return Result.ok(staffService.getMyProfile(userUuid));
    }

    @PutMapping("/profile")
    public Result<StaffVO> updateMyProfile(@Valid @RequestBody UpdateStaffProfileDTO dto,
                                           @RequestAttribute String userUuid) {
        return Result.ok("资料更新成功", staffService.updateMyProfile(userUuid, dto));
    }
}
