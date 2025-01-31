package com.chat.commenter.schemas

import kotlinx.serialization.Serializable

@Serializable
data class User(
	val uid: String,
	val displayName: String,
	val email: String,
	val pic: String,
	val disabled: Boolean,
)
