package com.chat.commenter.api

import android.content.Context
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json

fun createHttpClient(context : Context) : HttpClient {
	return HttpClient(CIO) {
		install(HttpCookies) {
			storage = PersistentCookieStorage(context)
		}
		
		install(ContentNegotiation) {
			json()
		}
	}
}

const val apiUrl = "https://game-repeatedly-glowworm.ngrok-free.app/" // Change for production

suspend fun HttpClient.requestFromAPI(path : String, method : HttpMethod = HttpMethod.Get) : HttpResponse {
	return when (method) {
		HttpMethod.Get -> get(
			HttpRequestBuilder().apply {
				contentType(ContentType.Application.Json)
				url("$apiUrl$path")
			}
		)
		HttpMethod.Post -> post(
			HttpRequestBuilder().apply {
				contentType(ContentType.Application.Json)
				url("$apiUrl$path")
			}
		)
		else -> throw Exception("Invalid request method $method")
	}
}

suspend inline fun <reified T> HttpClient.requestFromAPI(path : String, method : HttpMethod = HttpMethod.Get, body : T?) : HttpResponse {
	return when (method) {
		HttpMethod.Get -> get(
			HttpRequestBuilder().apply {
				contentType(ContentType.Application.Json)
				url("$apiUrl$path")
				if (body != null) setBody(body)
			}
		)
		HttpMethod.Post -> post(
			HttpRequestBuilder().apply {
				contentType(ContentType.Application.Json)
				url("$apiUrl$path")
				if (body != null) setBody(body)
			}
		)
		else -> throw Exception("Invalid request method $method")
	}
}