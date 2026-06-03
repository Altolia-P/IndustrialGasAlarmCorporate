package com.niit.industrialgasalarmcorporate.infrastructure.notification;

import com.niit.industrialgasalarmcorporate.common.utils.MaskUtil;
import com.niit.industrialgasalarmcorporate.domain.notification.Notification;
import com.niit.industrialgasalarmcorporate.domain.notification.NotificationChannel;
import com.niit.industrialgasalarmcorporate.domain.notification.NotificationSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 阿里云短信发送器 — 对接阿里云 SMS API
 * <p>
 * 当前为桩实现，发送操作记录日志并返回 true。
 * 接入真实阿里云 SDK 时替换 send() 内部逻辑即可。
 */
@Slf4j
@Component
public class AliyunSender implements NotificationSender {

    @Override
    public boolean send(Notification notification) {
        try {
            log.info("[AliyunSMS] 发送短信通知: phone={}, content={}",
                    MaskUtil.recipient(notification.getRecipient()),
                    MaskUtil.content(notification.getContent()));
            // TODO: 接入阿里云 SMS SDK
            return true;
        } catch (Exception e) {
            log.error("[AliyunSMS] 发送失败: recipient={}",
                    MaskUtil.recipient(notification.getRecipient()), e);
            return false;
        }
    }

    @Override
    public NotificationChannel supportedChannel() {
        return NotificationChannel.SMS;
    }
}
