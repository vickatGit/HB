package com.example.habit.data.network.model.IsUserFollowingModel

data class UserDetailsModel(
	val isFollowing: Boolean? = null,
	val user: User? = null
)

data class Follows(
	val v: Int? = null,
	val id: String? = null,
	val email: String? = null,
	val username: String? = null
)

data class User(
	val follows: Follows? = null,
	val id: String? = null,
	val to: String? = null
)

