package org.psl.fidouaf.service;

import org.psl.fidouaf.entity.SendPushNotification;
import org.springframework.stereotype.Service;

@Service
public interface PushNotificationService {
	public void sendPushNotifications(SendPushNotification pushData);
}
