package com.niit.industrialgasalarmcorporate.infrastructure.mq;

import com.niit.industrialgasalarmcorporate.domain.device.Device;
import com.niit.industrialgasalarmcorporate.domain.device.DeviceRepository;
import com.niit.industrialgasalarmcorporate.domain.device.DeviceStatus;
import com.niit.industrialgasalarmcorporate.infrastructure.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeviceEventConsumer {

    private final DeviceRepository deviceRepository;

    @RabbitListener(queues = RabbitMQConfig.DEVICE_ONLINE_QUEUE)
    public void handleDeviceOnline(Map<String, Object> msg) {
        try {
            String deviceUuid = (String) msg.get("deviceUuid");
            if (deviceUuid == null) return;

            Device device = deviceRepository.findById(deviceUuid).orElse(null);
            if (device == null) {
                log.warn("设备不存在，忽略上线消息: deviceUuid={}", deviceUuid);
                return;
            }
            if (device.getStatus() == DeviceStatus.OFFLINE) {
                device.markOnline();
                deviceRepository.save(device);
                log.info("设备恢复在线: deviceUuid={}", deviceUuid);
            }
        } catch (Exception e) {
            log.warn("处理设备上线消息失败（DB/Redis 不可用），忽略该消息: {}", e.getMessage());
        }
    }

    @RabbitListener(queues = RabbitMQConfig.DEVICE_OFFLINE_QUEUE)
    public void handleDeviceOffline(Map<String, Object> msg) {
        try {
            String deviceUuid = (String) msg.get("deviceUuid");
            if (deviceUuid == null) return;

            Device device = deviceRepository.findById(deviceUuid).orElse(null);
            if (device == null) {
                log.warn("设备不存在，忽略离线消息: deviceUuid={}", deviceUuid);
                return;
            }
            if (device.getStatus() == DeviceStatus.NORMAL || device.getStatus() == DeviceStatus.ABNORMAL) {
                device.markOffline();
                deviceRepository.save(device);
                log.info("设备标记离线: deviceUuid={}", deviceUuid);
            }
        } catch (Exception e) {
            log.warn("处理设备离线消息失败（DB/Redis 不可用），忽略该消息: {}", e.getMessage());
        }
    }
}
