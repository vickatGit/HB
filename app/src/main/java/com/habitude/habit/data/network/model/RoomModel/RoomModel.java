package com.habitude.habit.data.network.model.RoomModel;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RoomModel{

	@SerializedName("data")
	private List<Room> rooms;

	public List<Room> getData(){
		return rooms;
	}
}