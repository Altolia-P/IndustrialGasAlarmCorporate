package com.niit.industrialgasalarmcorporate.interfaces.admin;

import com.niit.industrialgasalarmcorporate.application.auth.dto.LoginDTO;
import com.niit.industrialgasalarmcorporate.application.auth.service.AuthService;
import com.niit.industrialgasalarmcorporate.application.auth.vo.CaptchaVO;
import com.niit.industrialgasalarmcorporate.application.auth.vo.LoginResultVO;
import com.niit.industrialgasalarmcorporate.application.auth.vo.UserVO;
import com.niit.industrialgasalarmcorporate.common.base.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public Result<LoginResultVO> login(@Valid @RequestBody LoginDTO dto) {
        return Result.ok("登录成功", authService.login(dto));
    }

    @GetMapping("/captcha")
    public Result<CaptchaVO> captcha() {
        return Result.ok(authService.generateCaptcha());
    }

    @GetMapping("/currentUser")
    public Result<UserVO> currentUser(@RequestAttribute String userUuid) {
        return Result.ok(authService.getCurrentUser(userUuid));
    }
}
