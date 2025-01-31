package com.chat.commenter.api

import android.content.Context
import android.content.SharedPreferences
import io.ktor.client.plugins.cookies.CookiesStorage
import io.ktor.client.plugins.cookies.matches
import io.ktor.http.Cookie
import io.ktor.http.Url
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class PersistentCookieStorage(context : Context) : CookiesStorage {
	private val preferences: SharedPreferences = context.getSharedPreferences("cookies", Context.MODE_PRIVATE)
	private val mutex = Mutex()
	private var cookies: MutableList<Cookie> = loadCookies()
	
	override suspend fun addCookie(requestUrl: Url, cookie: Cookie) {
		cookies.removeIf { it.name == cookie.name && it.matches(requestUrl) }
		cookies.add(cookie.copy(domain = requestUrl.host))
		println("ADDED A COOKIE: NAME=${cookie.name}, URL=$requestUrl, VALUE=${cookie.value}")
		saveCookies()
	}
	
	override fun close() {
		cookies = mutableListOf()
		saveCookies()
	}
	
	override suspend fun get(requestUrl: Url): List<Cookie> = mutex.withLock {
		cookies.filter { it.matches(requestUrl) }
	}
	
	private fun saveCookies() {
		val json = Json.encodeToString(cookies)
		preferences.edit().putString("cookies", json).apply()
	}
	
	private fun loadCookies(): MutableList<Cookie> {
		val json = preferences.getString("cookies", null) ?: "[]"
		println("LOADED COOKIES FROM STORAGE")
		println(Json.decodeFromString<MutableList<Cookie>>(json))
		return Json.decodeFromString(json)
	}
}