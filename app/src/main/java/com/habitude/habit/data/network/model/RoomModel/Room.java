package com.habitude.habit.data.network.model.RoomModel;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Room {
	private List<String> members;
	private int v;
	private String admin;

	public String getRoomThumbUrl() {
		return roomThumbUrl;
	}

	@SerializedName("_id")
	private String id;
	private String roomName;
	private String roomType;

	@SerializedName("roomThumb")
	private String roomThumbUrl;
	@SerializedName("adminImage")
	private String adminImageUrl;

	public String getAdminImageUrl() {
		return adminImageUrl;
	}

	public List<String> getMembers(){
		return members;
	}

	public int getV(){
		return v;
	}

	public String getAdmin(){
		return admin;
	}

	public String getId(){
		return id;
	}

	public String getRoomName(){
		return roomName;
	}

	public String getRoomType(){
		return roomType;
	}
}