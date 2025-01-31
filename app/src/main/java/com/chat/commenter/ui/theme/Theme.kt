package com.chat.commenter.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.colorResource
import androidx.core.view.WindowCompat
import com.chat.commenter.R

@Composable
fun CommenterTheme(
	darkTheme: Boolean = isSystemInDarkTheme(),
	// Dynamic color is available on Android 12+
	content: @Composable () -> Unit
) {
	val colorScheme = when {
		darkTheme -> darkColorScheme(
			primary = colorResource(id = R.color.dark_app_bar),
			secondary = colorResource(id = R.color.primary),
			tertiary = colorResource(id = R.color.secondary),
			background = colorResource(id = R.color.dark_surface),
			surface = colorResource(id = R.color.dark_surface),
			error = colorResource(id = R.color.red),
			onPrimary = colorResource(id = R.color.white),
			onSecondary = colorResource(id = R.color.black),
			onTertiary = colorResource(id = R.color.black),
			onBackground = colorResource(id = R.color.white),
			onSurface = colorResource(id = R.color.white),
			onError = colorResource(id = R.color.black),
			scrim = colorResource(id = R.color.link),
		)
		else -> lightColorScheme(
			primary = colorResource(id = R.color.primary),
			secondary = colorResource(id = R.color.secondary),
			tertiary = colorResource(id = R.color.secondary),
			background = colorResource(id = R.color.white),
			surface = colorResource(id = R.color.white),
			error = colorResource(id = R.color.red),
			onPrimary = colorResource(id = R.color.black),
			onSecondary = colorResource(id = R.color.black),
			onTertiary = colorResource(id = R.color.black),
			onBackground = colorResource(id = R.color.black),
			onSurface = colorResource(id = R.color.black),
			onError = colorResource(id = R.color.black),
			scrim = colorResource(id = R.color.link),
		)
	}
	val statusBarColour = if (darkTheme)
		colorResource(id = R.color.dark_status_bar)
	else
		colorResource(id = R.color.primary_dark)
	val view = LocalView.current
	if (!view.isInEditMode) {
		SideEffect {
			val window = (view.context as Activity).window
			window.statusBarColor = statusBarColour.toArgb()
			WindowCompat.getInsetsController(
				window,
				view
			).isAppearanceLightStatusBars = !darkTheme
		}
	}
	
	MaterialTheme(
		colorScheme = colorScheme,
		typography = Typography,
		content = content
	)
}