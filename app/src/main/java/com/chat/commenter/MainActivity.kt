package com.chat.commenter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.chat.commenter.compose.profile.Profile
import com.chat.commenter.compose.auth.Auth
import com.chat.commenter.compose.home.Home
import com.chat.commenter.compose.settings.Settings
import com.chat.commenter.state.AppViewModel
import com.chat.commenter.ui.theme.CommenterTheme
import org.koin.androidx.compose.KoinAndroidContext
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		
		setContent {
			KoinAndroidContext {
				Wrapper {
					Navigator()
				}
			}
		}
	}
}

@Composable
fun Wrapper(
	viewModel: AppViewModel = koinViewModel(),
	content: @Composable () -> Unit
) {
	val textSelectionColours = TextSelectionColors(
		handleColor = colorResource(id = R.color.primary),
		backgroundColor = colorResource(id = R.color.primary).copy(alpha = 0x99.toFloat() / 256),
	)
	val ui by viewModel.uiState.collectAsState()
	val uiMode = ui.uiMode
	val context = LocalContext.current
	
	LaunchedEffect(Unit) {
		viewModel.loadDefaults(context)
	}
	
	CommenterTheme(
		darkTheme = uiMode == "dark" || (uiMode == "system" && isSystemInDarkTheme())
	) {
		CompositionLocalProvider(
			value = LocalTextSelectionColors provides textSelectionColours,
			content = content
		)
	}
}

enum class Page {
	Home,
	Auth,
	Settings,
	Profile;
	
	val nameId
		get() = when (this) {
			Home -> R.string.home
			Auth -> R.string.auth
			Settings -> R.string.settings
			Profile -> R.string.profile
		}
	
	val route
		get() = when (this) {
			Home -> "home"
			Auth -> "auth"
			Settings -> "settings"
			Profile -> "profile"
		}
}

val LocalNavController = staticCompositionLocalOf<NavController> {
	error("No NavController provided")
}

@Composable
fun Navigator() {
	val navController = rememberNavController()
	
	CompositionLocalProvider(value = LocalNavController provides navController) {
		NavHost(
			navController = navController,
			startDestination = Page.Auth.route,
		) {
			composable(route = Page.Auth.route) {
				Auth()
			}
			composable(route = Page.Home.route) {
				Home()
			}
			composable(route = Page.Settings.route) {
				Settings()
			}
			composable(route = Page.Profile.route) {
				Profile()
			}
		}
	}
}
