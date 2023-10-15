package com.example.habit.data.network.model.RoomModel;

import java.util.List;

public class Room {
	private List<String> members;
	private int v;
	private String admin;
	private String id;
	private String roomName;
	private String roomType;

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