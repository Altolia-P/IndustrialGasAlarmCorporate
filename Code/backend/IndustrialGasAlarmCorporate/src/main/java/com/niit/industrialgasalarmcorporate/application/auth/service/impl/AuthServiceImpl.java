package com.niit.industrialgasalarmcorporate.application.auth.service.impl;

import com.niit.industrialgasalarmcorporate.application.auth.dto.LoginDTO;
import com.niit.industrialgasalarmcorporate.application.auth.dto.RegisterDTO;
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

    @Override
    @Transactional
    public LoginResultVO login(LoginDTO dto) {
        // Validate captcha if provided
        if (dto.getCaptcha() != null && dto.getCaptchaToken() != null) {
            String storedText = captchaRepository.getAndRemove(dto.getCaptchaToken());
            if (storedText == null || !storedText.equalsIgnoreCase(dto.getCaptcha())) {
                throw new BusinessException(ErrorCode.VALIDATION_ERROR, "验证码错误或已过期");
            }
        }

        User user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new UserNotFoundException(dto.getUsername()));

        LoginResult result = user.login(dto.getPassword(), passwordHasher);
        userRepository.save(user);

        if (user.justLocked()) {
            eventBus.publish(new AccountLockedEvent(user.getUserUuid()));
        }

        if (!result.isSuccess()) {
            if (user.getLocked()) {
                throw new AccountLockedException(user.getUserUuid());
            }
            throw new InvalidPasswordException(result.getReason());
        }

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
        userRepository.findByUsername(dto.getUsername()).ifPresent(u -> {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "用户名已存在");
        });

        User user = new User(dto.getUsername(), passwordHasher.hash(dto.getPassword()),
                dto.getPhone(), dto.getCompany(), "USER");
        userRepository.save(user);
        log.info("新用户注册: username={}, uuid={}", user.getUsername(), user.getUserUuid());
    }

    @Override
    @Transactional(readOnly = true)
    public UserVO getCurrentUser(String userUuid) {
        User user = userRepository.findById(userUuid)
                .orElseThrow(() -> new UserNotFoundException(userUuid));
        return UserAssembler.toVO(user);
    }
}
