package com.jzprog.chatapp.src.model;

public class NotificationDTO {
	
	private String pushPublicKey;
	private String pushSubInfo;
	private Integer subId;
	
	public String getPushPublicKey() {
		return pushPublicKey;
	}
	
	public void setPushPublicKey(String pushPublicKey) {
		this.pushPublicKey = pushPublicKey;
	}
	
	public String getPushSubInfo() {
		return pushSubInfo;
	}
	
	public void setPushSubInfo(String pushSubInfo) {
		this.pushSubInfo = pushSubInfo;
	}
	
	public Integer getSubId() {
		return subId;
	}

	public void setSubId(Integer subId) {
		this.subId = subId;
	}

	@Override
	public String toString() {
		return "NotificationDTO [pushPublicKey=" + pushPublicKey + ", pushSubInfo=" + pushSubInfo + ", subId=" + subId + "]";
	}
}
