package com.jzprog.chatapp.src.services;

import org.json.JSONException;
import com.jzprog.chatapp.src.model.User;
import com.jzprog.chatapp.src.utils.SystemMessages.NotificationCategories;

public interface PushNotificationService {
	
	Integer storePushNotificationSubInfo(String jsonString, Integer userId) throws JSONException;
    void deletePushNotificationSubInfo(Integer subId);
    void sendPushNotificationToObservers(User user, NotificationCategories type,Integer convId) throws Exception;
    String providePublicKey();
}
