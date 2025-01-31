package com.chat.commenter.state

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.lifecycle.ViewModel
import com.chat.commenter.api.createHttpClient
import com.chat.commenter.schemas.User
import io.ktor.client.HttpClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

data class UserState(
	val user: User? = null
)

interface UserStateHolder {
	val userState: MutableStateFlow<UserState>
	fun updateUser(user: User)
	fun logout()
}

class UserStateHolderImpl : UserStateHolder {
	override val userState: MutableStateFlow<UserState>
		get() = MutableStateFlow(UserState())
	
	override fun updateUser(user: User) {
		userState.update { UserState(user) }
	}
	
	override fun logout() {
		userState.update { UserState() }
	}
}

data class UIState(
	val uiMode: String = "system",
	val tsf: Double = 1.0
)

interface UIStateHolder {
	val uiState: MutableStateFlow<UIState>
	fun loadState(context: Context)
	fun updateState(state: UIState, context: Context)
}

class UIStateHolderImpl : UIStateHolder {
	override val uiState: MutableStateFlow<UIState>
		get() = MutableStateFlow(UIState())
	
	override fun loadState(context: Context) {
		val preferences = context.getSharedPreferences("ui", MODE_PRIVATE)
		uiState.update {
			UIState(
				preferences.getString("uiMode", "system") ?: "system",
				preferences.getFloat("tsf", 1.0f).toDouble(),
			)
		}
	}
	
	override fun updateState(state: UIState, context: Context) {
		uiState.update { state }
	}
}

interface HttpClientStateHolder {
	val clientState : MutableStateFlow<HttpClient?>
	fun createClient(context: Context)
}

class HttpClientStateHolderImpl : HttpClientStateHolder {
	override val clientState: MutableStateFlow<HttpClient?> = MutableStateFlow(null)
	
	override fun createClient(context: Context) {
		clientState.update { createHttpClient(context) }
	}
}

val appModule = module {
	singleOf(::UserStateHolderImpl) { bind<UserStateHolder>() }
	singleOf(::UIStateHolderImpl) { bind<UIStateHolder>() }
	singleOf(::HttpClientStateHolderImpl) { bind<HttpClientStateHolder>() }
	viewModelOf(::AppViewModel)
}

class AppViewModel(private val userState: UserStateHolder, private val uiState: UIStateHolder, private val httpClient: HttpClientStateHolder) : ViewModel() {
	fun loadDefaults(context: Context) {
		uiState.loadState(context)
		httpClient.createClient(context)
		println("LOADED DEFAULTS")
	}
	
	fun getUIMode() : String = uiState.uiState.value.uiMode
	fun getTSF() : Double = uiState.uiState.value.tsf
	fun getUser() : User? = userState.userState.value.user
	fun getHttpClient() : HttpClient? = httpClient.clientState.value
}