package com.chat.commenter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.chat.commenter.compose.auth.Auth
import com.chat.commenter.compose.home.Home
import com.chat.commenter.state.AppViewModel
import com.chat.commenter.state.UIStateHolder
import com.chat.commenter.ui.theme.CommenterTheme
import org.koin.androidx.compose.KoinAndroidContext
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

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
	
	viewModel.loadDefaults(LocalContext.current)

	CommenterTheme(
		darkTheme = viewModel.getUIMode() == "dark" || (viewModel.getUIMode() == "system" && isSystemInDarkTheme())
	) {
		CompositionLocalProvider(
			value = LocalTextSelectionColors provides textSelectionColours,
			content = content
		)
	}
}

sealed class Page(val route: String) {
	data object Home: Page("home")
	data object Auth: Page("auth")
}

@Composable
fun Navigator(viewModel : AppViewModel = koinViewModel()) {
	val navController = rememberNavController()
	
	NavHost(
		navController = navController,
		startDestination = Page.Auth.route,
	) {
		composable(route = Page.Auth.route) {
			Auth(navController = navController)
		}
		composable(route = Page.Home.route) {
			Home()
		}
	}
}
