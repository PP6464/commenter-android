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
data class EmptyResponse(
	val message : String,
	val code : Int,
)

@Serializable
data class UserResponse(
	val message : String,
	val code : Int,
	val payload : User? = null,
)

@Serializable
data class ProfileUpdateBody(
	val uid : String,
	val displayName : String? = null,
	val email : String? = null,
	val password : String? = null,
	val pic : String? = null,
	val status: String? = null,
	val hasPicFile : Boolean = false
)