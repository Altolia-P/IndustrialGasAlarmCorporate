package com.niit.industrialgasalarmcorporate.assembler;

import com.niit.industrialgasalarmcorporate.application.auth.vo.UserVO;
import com.niit.industrialgasalarmcorporate.domain.auth.User;

import java.time.format.DateTimeFormatter;

public final class UserAssembler {

    private UserAssembler() {
    }

    public static UserVO toVO(User user) {
        UserVO vo = new UserVO();
        vo.setUserUuid(user.getUserUuid());
        vo.setUsername(user.getUsername());
        vo.setPhone(user.getPhone());
        vo.setCompany(user.getCompany());
        vo.setLocked(user.getLocked());
        if (user.getLastLoginAt() != null) {
            vo.setLastLoginAt(user.getLastLoginAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        return vo;
    }
}
