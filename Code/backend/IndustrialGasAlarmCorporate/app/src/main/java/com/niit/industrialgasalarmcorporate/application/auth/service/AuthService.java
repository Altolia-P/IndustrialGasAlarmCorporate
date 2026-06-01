package com.niit.industrialgasalarmcorporate.application.auth.service;

import com.niit.industrialgasalarmcorporate.application.auth.dto.*;
import com.niit.industrialgasalarmcorporate.application.auth.vo.CaptchaVO;
import com.niit.industrialgasalarmcorporate.application.auth.vo.LoginResultVO;
import com.niit.industrialgasalarmcorporate.application.auth.vo.UserVO;

public interface AuthService {

    LoginResultVO login(LoginDTO dto);

    CaptchaVO generateCaptcha();

    void register(RegisterDTO dto);

    UserVO getCurrentUser(String userUuid);

    void logout(String token);

    void resetPassword(ResetPasswordDTO dto);

    void resetMyPassword(String userUuid, UserResetPasswordDTO dto);

    UserVO updateProfile(String userUuid, UpdateProfileDTO dto);
}
