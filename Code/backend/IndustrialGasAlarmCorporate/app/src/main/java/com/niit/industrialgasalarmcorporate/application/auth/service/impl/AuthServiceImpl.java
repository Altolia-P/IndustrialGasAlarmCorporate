package com.niit.industrialgasalarmcorporate.application.auth.service.impl;

import com.niit.industrialgasalarmcorporate.application.auth.dto.*;
import com.niit.industrialgasalarmcorporate.application.auth.service.AuthService;
import com.niit.industrialgasalarmcorporate.application.auth.vo.CaptchaVO;
import com.niit.industrialgasalarmcorporate.application.auth.vo.LoginResultVO;
import com.niit.industrialgasalarmcorporate.application.auth.vo.UserVO;
import com.niit.industrialgasalarmcorporate.assembler.UserAssembler;
import com.niit.industrialgasalarmcorporate.common.enums.ErrorCode;
import com.niit.industrialgasalarmcorporate.common.exception.AccountLockedException;
import com.niit.industrialgasalarmcorporate.common.exception.BusinessException;
import com.niit.industrialgasalarmcorporate.common.exception.InvalidPasswordException;
import com.niit.industrialgasalarmcorporate.common.exception.UserNotFoundException;
import com.niit.industrialgasalarmcorporate.common.utils.CaptchaGenerator;
import com.niit.industrialgasalarmcorporate.common.utils.JwtUtil;
import com.niit.industrialgasalarmcorporate.domain.auth.LoginResult;
import com.niit.industrialgasalarmcorporate.domain.auth.PasswordHasher;
import com.niit.industrialgasalarmcorporate.domain.auth.User;
import com.niit.industrialgasalarmcorporate.domain.auth.UserRepository;
import com.niit.industrialgasalarmcorporate.domain.event.AccountLockedEvent;
import com.niit.industrialgasalarmcorporate.domain.event.EventBus;
import com.niit.industrialgasalarmcorporate.infrastructure.redis.CaptchaRepository;
import com.niit.industrialgasalarmcorporate.infrastructure.redis.JwtBlacklistRepository;
import com.niit.industrialgasalarmcorporate.infrastructure.redis.LoginRateLimitRepository;
import com.niit.industrialgasalarmcorporate.infrastructure.redis.RegisterRateLimitRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final EventBus eventBus;
    private final PasswordHasher passwordHasher;
    private final CaptchaGenerator captchaGenerator;
    private final CaptchaRepository captchaRepository;
    private final JwtBlacklistRepository jwtBlacklistRepository;
    private final LoginRateLimitRepository loginRateLimitRepository;
    private final RegisterRateLimitRepository registerRateLimitRepository;
    private final HttpServletRequest request;

    @Override
    @Transactional(noRollbackFor = {InvalidPasswordException.class, AccountLockedException.class})
    public LoginResultVO login(LoginDTO dto) {
        String ip = request.getRemoteAddr();
        if (!loginRateLimitRepository.tryAcquire(ip)) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "登录尝试过于频繁，请1分钟后再试");
        }

        // Captcha always required
        if (dto.getCaptcha() == null || dto.getCaptchaToken() == null) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "验证码不能为空");
        }
        String storedText = captchaRepository.getAndRemove(dto.getCaptchaToken());
        if (storedText == null || !storedText.equalsIgnoreCase(dto.getCaptcha())) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "验证码错误或已过期");
        }

        User user = userRepository.findByUsername(dto.getUsername())
                .orElse(null);

        if (user == null) {
            log.warn("登录失败: 用户不存在, username={}, ip={}", maskUsername(dto.getUsername()), ip);
            throw new InvalidPasswordException("用户名或密码错误");
        }

        LoginResult result = user.login(dto.getPassword(), passwordHasher);
        userRepository.save(user);

        if (user.justLocked()) {
            eventBus.publish(new AccountLockedEvent(user.getUserUuid()));
        }

        if (!result.isSuccess()) {
            if (user.getLocked()) {
                log.warn("登录失败: 账号已锁定, username={}, ip={}", maskUsername(dto.getUsername()), ip);
                throw new AccountLockedException(user.getUserUuid());
            }
            log.warn("登录失败: 密码错误, username={}, ip={}", maskUsername(dto.getUsername()), ip);
            throw new InvalidPasswordException(result.getReason());
        }

        log.info("登录成功: username={}, role={}", maskUsername(user.getUsername()), user.getRole());
        String token = jwtUtil.generateToken(user.getUserUuid(), user.getUsername(), user.getRole());
        return new LoginResultVO(token, user.getUserUuid(), user.getUsername(), user.getRole());
    }

    @Override
    public CaptchaVO generateCaptcha() {
        String text = captchaGenerator.generateText();
        String token = UUID.randomUUID().toString();
        captchaRepository.store(token, text);
        String image = captchaGenerator.generateBase64Image(text);
        return new CaptchaVO(image, token);
    }

    @Override
    @Transactional
    public void register(RegisterDTO dto) {
        String ip = request.getRemoteAddr();
        if (!registerRateLimitRepository.tryAcquire(ip)) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "注册过于频繁，请1小时后再试");
        }
        userRepository.findByUsername(dto.getUsername()).ifPresent(u -> {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "用户名已存在");
        });

        User user = new User(dto.getUsername(), passwordHasher.hash(dto.getPassword()),
                dto.getPhone(), dto.getCompany(), "USER");
        userRepository.save(user);
        log.info("新用户注册: username={}, uuid={}", maskUsername(user.getUsername()), user.getUserUuid());
    }

    @Override
    @Transactional(readOnly = true)
    public UserVO getCurrentUser(String userUuid) {
        User user = userRepository.findById(userUuid)
                .orElseThrow(() -> new UserNotFoundException(userUuid));
        return UserAssembler.toVO(user);
    }

    @Override
    public void logout(String token) {
        try {
            jwtUtil.parseToken(token);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "Token 无效或已过期");
        }
        long remainingMillis = jwtUtil.getRemainingMillis(token);
        if (remainingMillis > 0) {
            jwtBlacklistRepository.add(token, remainingMillis);
        }
        log.info("用户已登出，Token 已加入黑名单");
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordDTO dto) {
        if (dto.getAdminPassword() == null || dto.getAdminPassword().isBlank()) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "管理员密码不能为空");
        }
        User admin = userRepository.findById(request.getAttribute("userUuid").toString())
                .orElseThrow(() -> new UserNotFoundException("管理员"));
        if (!passwordHasher.matches(dto.getAdminPassword(), admin.getPasswordHash())) {
            throw new InvalidPasswordException("管理员密码不正确");
        }
        User user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new UserNotFoundException(dto.getUsername()));
        user.changePassword(dto.getNewPassword(), passwordHasher);
        userRepository.save(user);
        log.info("管理员重置密码: username={}, uuid={}", maskUsername(user.getUsername()), user.getUserUuid());
    }

    @Override
    @Transactional
    public void resetMyPassword(String userUuid, UserResetPasswordDTO dto) {
        User user = userRepository.findById(userUuid)
                .orElseThrow(() -> new UserNotFoundException(userUuid));
        if (!passwordHasher.matches(dto.getOldPassword(), user.getPasswordHash())) {
            throw new InvalidPasswordException("旧密码不正确");
        }
        user.changePassword(dto.getNewPassword(), passwordHasher);
        userRepository.save(user);
        log.info("用户自主修改密码: uuid={}", userUuid);
    }

    @Override
    @Transactional
    public UserVO updateProfile(String userUuid, UpdateProfileDTO dto) {
        User user = userRepository.findById(userUuid)
                .orElseThrow(() -> new UserNotFoundException(userUuid));
        user.updateProfile(dto.getPhone(), dto.getCompany());
        userRepository.save(user);
        log.info("用户资料已更新: uuid={}", userUuid);
        return UserAssembler.toVO(user);
    }

    private String maskUsername(String username) {
        if (username == null || username.length() <= 3) return "***";
        return username.charAt(0) + "***" + username.charAt(username.length() - 1);
    }
}
