package com.niit.industrialgasalarmcorporate.assembler;

import com.niit.industrialgasalarmcorporate.application.auth.vo.UserVO;
import com.niit.industrialgasalarmcorporate.domain.auth.User;

public final class UserAssembler {

    private UserAssembler() {
    }

    public static UserVO toVO(User user) {
        UserVO vo = new UserVO();
        vo.setUserUuid(user.getUserUuid());
        vo.setUsername(user.getUsername());
        vo.setLocked(user.getLocked());
        return vo;
    }
}
