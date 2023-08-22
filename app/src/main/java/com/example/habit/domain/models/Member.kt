package com.example.habit.domain.models

import com.google.gson.annotations.SerializedName

data class Member(
	@SerializedName("_id")
	val userId: String? = null,
	val username: String? = null,
)