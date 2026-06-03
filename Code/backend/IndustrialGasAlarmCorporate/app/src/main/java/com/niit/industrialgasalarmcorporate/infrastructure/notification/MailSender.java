package com.niit.industrialgasalarmcorporate.infrastructure.notification;

import com.niit.industrialgasalarmcorporate.common.utils.MaskUtil;
import com.niit.industrialgasalarmcorporate.domain.notification.Notification;
import com.niit.industrialgasalarmcorporate.domain.notification.NotificationChannel;
import com.niit.industrialgasalarmcorporate.domain.notification.NotificationSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 邮件发送器 — 对接 SMTP / 邮件服务
 * <p>
 * 当前为桩实现，发送操作记录日志并返回 true。
 * 接入真实邮件服务时替换 send() 内部逻辑即可。
 */
@Slf4j
@Component
public class MailSender implements NotificationSender {

    @Override
    public boolean send(Notification notification) {
        try {
            log.info("[MailSender] 发送邮件通知: email={}, content={}",
                    MaskUtil.recipient(notification.getRecipient()),
                    MaskUtil.content(notification.getContent()));
            // TODO: 接入邮件发送服务
            return true;
        } catch (Exception e) {
            log.error("[MailSender] 发送失败: recipient={}",
                    MaskUtil.recipient(notification.getRecipient()), e);
            return false;
        }
    }

    @Override
    public NotificationChannel supportedChannel() {
        return NotificationChannel.EMAIL;
    }
}
