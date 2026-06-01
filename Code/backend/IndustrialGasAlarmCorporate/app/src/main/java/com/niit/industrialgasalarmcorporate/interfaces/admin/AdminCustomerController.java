package com.niit.industrialgasalarmcorporate.interfaces.admin;

import com.niit.industrialgasalarmcorporate.application.customer360.service.Customer360Service;
import com.niit.industrialgasalarmcorporate.application.customer360.vo.Customer360VO;
import com.niit.industrialgasalarmcorporate.common.base.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminCustomerController {

    private final Customer360Service customer360Service;

    @GetMapping("/customers/360")
    public Result<Customer360VO> getCustomer360(@RequestParam String phone) {
        return Result.ok(customer360Service.getCustomer360(phone));
    }
}
