package com.niit.industrialgasalarmcorporate.interfaces.admin;

import com.niit.industrialgasalarmcorporate.application.auth.dto.LoginDTO;
import com.niit.industrialgasalarmcorporate.application.auth.dto.ResetPasswordDTO;
import com.niit.industrialgasalarmcorporate.application.auth.service.AuthService;
import com.niit.industrialgasalarmcorporate.application.auth.vo.CaptchaVO;
import com.niit.industrialgasalarmcorporate.application.auth.vo.LoginResultVO;
import com.niit.industrialgasalarmcorporate.application.auth.vo.UserVO;
import com.niit.industrialgasalarmcorporate.common.base.Result;
import com.niit.industrialgasalarmcorporate.common.enums.ErrorCode;
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

    @PostMapping("/logout")
    public Result<Void> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ") || authHeader.length() < 7) {
            return Result.fail(ErrorCode.UNAUTHORIZED.getCode(), "未登录");
        }
        String token = authHeader.substring(7);
        authService.logout(token);
        return Result.ok("已登出", null);
    }

    @PutMapping("/resetPassword")
    public Result<Void> resetPassword(@Valid @RequestBody ResetPasswordDTO dto) {
        authService.resetPassword(dto);
        return Result.ok("密码重置成功", null);
    }
}
