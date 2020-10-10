package com.jzprog.chatapp.src.services;

import java.security.Security;
import java.util.List;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import com.jzprog.chatapp.src.advices.LogMethodInfo;
import com.jzprog.chatapp.src.database.PushNotificationsRepository;
import com.jzprog.chatapp.src.model.PushNotification;
import com.jzprog.chatapp.src.model.User;
import com.jzprog.chatapp.src.utils.SystemMessages;
import com.jzprog.chatapp.src.utils.SystemMessages.NotificationCategories;

import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;

@Service
public class PushNotificationServiceImpl implements PushNotificationService {
	
	@Value("${push.notifications.public.key}")
	private String publicKey;
	
	@Value("${push.notifications.private.key}")
	private String privateKey;
	
	@Autowired
	PushNotificationsRepository pushRepo;

	@LogMethodInfo
	@Override
	@Transactional
	public Integer storePushNotificationSubInfo(String jsonString, Integer userId) throws JSONException {
		JSONObject jsonObj = new JSONObject(jsonString);
		String endpoint = jsonObj.getString("endpoint");
		JSONObject keys = jsonObj.getJSONObject("keys");
		String userKey = keys.getString("p256dh");
		String userAuth = keys.getString("auth");
		PushNotification pushNotification = new PushNotification(endpoint, userId, userKey, userAuth);
		pushRepo.save(pushNotification);
		return pushNotification.getId();
	}

	@LogMethodInfo
	@Override
	@Transactional
	public void deletePushNotificationSubInfo(Integer subId) {
		pushRepo.delete(subId);
	}

	@LogMethodInfo
	@Override
	public void sendPushNotificationToObservers(User user, NotificationCategories type) throws Exception {
		Security.addProvider(new BouncyCastleProvider());
		PushService pushService = new PushService(publicKey, privateKey, SystemMessages.PUSH_NOTIFICATION_SUBJECT);
		String payload = createPushServicePayload(user, type);
		List<PushNotification> activeSubscribers = getActiveSubscribers(user);
		for (PushNotification activeSubscriber : activeSubscribers) {
			pushService.send(new Notification(activeSubscriber.getEndpoint(), activeSubscriber.getP256dh(),activeSubscriber.getAuth(), payload));
		}
	}

	@LogMethodInfo
	@Override
	public String providePublicKey() {
		return publicKey;
	}
	
	@LogMethodInfo
	@Transactional
	private List<PushNotification> getActiveSubscribers(User user) {
		return (List<PushNotification>) pushRepo.findActiveSubscribers(user.getId());
	}
	
	@LogMethodInfo
	private String createPushServicePayload(User user, NotificationCategories type) throws JSONException {
		return new JSONObject()
				.put("title", type.getTitle())
				.put("message", String.format(type.getMessage(), user.getUsername()))
				.put("icon", type.getImage())
				.put("badge", type.getImage())
				.toString();
	}

}
