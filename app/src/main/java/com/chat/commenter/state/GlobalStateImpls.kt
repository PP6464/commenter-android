package com.chat.commenter.state

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import com.chat.commenter.api.createHttpClient
import com.chat.commenter.schemas.User
import io.ktor.client.HttpClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
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
	override val userState = MutableStateFlow(UserState())
	
	override fun updateUser(user: User) {
		userState.update { UserState(user) }
	}
	
	override fun logout() {
		userState.update { UserState() }
	}
}

data class UIState(
	val uiMode: String = "system",
	val tsf: Float = 1.0F
)

interface UIStateHolder {
	val uiState: MutableStateFlow<UIState>
	fun loadState(context: Context)
	fun updateState(state: UIState)
	fun saveState()
}

class UIStateHolderImpl(private val context: Context) : UIStateHolder {
	override val uiState = MutableStateFlow(UIState())
	
	override fun loadState(context: Context) {
		val preferences = context.getSharedPreferences("ui", MODE_PRIVATE)
		uiState.update {
			UIState(
				preferences.getString("uiMode", "system") ?: "system",
				preferences.getFloat("tsf", 1.0f),
			)
		}
	}
	
	override fun updateState(state: UIState) {
		uiState.update { state }
		saveState()
	}
	
	override fun saveState() {
		val preferences = context.getSharedPreferences("ui", MODE_PRIVATE)
		preferences
			.edit {
				putString("uiMode", uiState.value.uiMode)
					.putFloat("tsf", uiState.value.tsf)
			}
	}
}

interface HttpClientStateHolder {
	val clientState: MutableStateFlow<HttpClient?>
	fun createClient(context: Context)
}

class HttpClientStateHolderImpl : HttpClientStateHolder {
	override val clientState: MutableStateFlow<HttpClient?> = MutableStateFlow(null)
	
	override fun createClient(context: Context) {
		clientState.update { createHttpClient(context) }
	}
}

val appModule = module {
	single<UserStateHolder> { UserStateHolderImpl() }
	single<UIStateHolder> { UIStateHolderImpl(androidContext()) }
	single<HttpClientStateHolder> { HttpClientStateHolderImpl() }
	viewModel { AppViewModel(get(), get(), get()) }
}

class AppViewModel(
	private val _userState: UserStateHolder,
	private val _uiState: UIStateHolder,
	private val _httpClient: HttpClientStateHolder,
) : ViewModel() {
	fun loadDefaults(context: Context) {
		_uiState.loadState(context)
		_httpClient.createClient(context)
		println("LOADED DEFAULTS")
	}
	
	val userState = _userState.userState.asStateFlow()
	val uiState = _uiState.uiState.asStateFlow()
	val clientState = _httpClient.clientState.asStateFlow()
	
	fun setUIMode(uiMode: String) = _uiState.updateState(_uiState.uiState.value.copy(uiMode = uiMode))
	fun setTSF(tsf: Float) = _uiState.updateState(_uiState.uiState.value.copy(tsf = tsf))
	fun setUser(user: User) = _userState.updateUser(user)
	fun logout() = _userState.logout()
}