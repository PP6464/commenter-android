package com.chat.commenter.api

import com.chat.commenter.schemas.User
import kotlinx.serialization.Serializable

@Serializable
data class SignUpBody(
	val email: String,
	val password: String,
	val displayName : String,
)

@Serializable
data class LoginBody(
	val email: String,
	val password: String,
)

@Serializable
data class NoPayloadResponseBody(
	val message : String,
	val code : Int,
)

@Serializable
data class UserResponseBody(
	val message : String,
	val code : Int,
	val payload : User? = null,
)