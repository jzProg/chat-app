package com.jzprog.chatapp.src.database;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import com.jzprog.chatapp.src.model.PushNotification;
import org.springframework.stereotype.Repository;

@Repository
public interface PushNotificationsRepository extends CrudRepository<PushNotification,Integer> {
	
	  @Query("from push_notification n where n.userId != :userId")
	  public List<PushNotification> findActiveSubscribers(@Param("userId") Integer userId);
}
