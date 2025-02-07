package com.chat.commenter.compose.auth

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chat.commenter.LocalNavController
import com.chat.commenter.Page
import com.chat.commenter.R
import com.chat.commenter.api.LoginBody
import com.chat.commenter.api.EmptyResponse
import com.chat.commenter.api.UserResponse
import com.chat.commenter.api.requestFromAPI
import com.chat.commenter.state.AppViewModel
import com.chat.commenter.ui.theme.Typography
import com.chat.commenter.ui.theme.montserrat
import io.ktor.client.call.body
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun Login(
	viewModel: AppViewModel = koinViewModel(),
	changeTab: () -> Unit,
) {
	// Text values
	var email by remember { mutableStateOf("") }
	var password by remember { mutableStateOf("") }
	// Show/Hide password
	var showPassword by remember { mutableStateOf(false) }
	// Check if text fields have focus
	var emailHasFocus by remember { mutableStateOf(false) }
	var passwordHasFocus by remember { mutableStateOf(false) }
	// Error messages for each text field
	var emailError by remember { mutableStateOf<String?>(null) }
	var passwordError by remember { mutableStateOf<String?>(null) }
	// UI
	val ui by viewModel.uiState.collectAsState()
	val tsf = ui.tsf
	val context = LocalContext.current
	var loading by remember { mutableStateOf(false) }
	// Coroutines
	val coroutineScope = rememberCoroutineScope()
	// NavController
	val navController = LocalNavController.current
	// HTTP
	val httpClient by viewModel.clientState.collectAsState()
	
	Column(
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center,
		modifier = Modifier
			.fillMaxSize()
			.verticalScroll(state = rememberScrollState())
	) {
		Image(
			painter = painterResource(id = R.drawable.commenter),
			contentDescription = null,
			modifier = Modifier
				.size(200.dp)
				.padding(16.dp)
				.clip(CircleShape)
				.border(
					width = 2.dp,
					shape = CircleShape,
					color = Color.Black,
				)
		)
		Text(
			text = stringResource(id = R.string.login),
			style = TextStyle(
				fontWeight = FontWeight.Bold,
				fontSize = 30.sp * tsf,
				fontFamily = montserrat,
			)
		)
		Spacer(modifier = Modifier.height(8.dp))
		OutlinedTextField(
			value = email,
			onValueChange = {
				email = it
				emailError = null
			},
			textStyle = TextStyle(
				fontSize = Typography.bodyLarge.fontSize * tsf,
				fontFamily = montserrat,
			),
			isError = emailError != null,
			singleLine = true,
			label = {
				Text(
					stringResource(id = R.string.enter_email),
					fontSize = Typography.bodyLarge.fontSize * tsf,
					fontFamily = montserrat,
				)
			},
			placeholder = {
				Text(
					stringResource(id = R.string.enter_email),
					fontSize = Typography.bodyLarge.fontSize * tsf,
				)
			},
			leadingIcon = {
				Icon(
					imageVector = Icons.Default.Email,
					contentDescription = null,
					tint =
					if (emailError != null)
						MaterialTheme.colorScheme.error
					else if (emailHasFocus)
						MaterialTheme.colorScheme.secondary
					else
						Color.Gray,
				)
			},
			colors = OutlinedTextFieldDefaults.colors(
				cursorColor = MaterialTheme.colorScheme.secondary,
				errorCursorColor = MaterialTheme.colorScheme.error,
				focusedBorderColor = MaterialTheme.colorScheme.secondary,
				errorBorderColor = MaterialTheme.colorScheme.error,
				focusedLabelColor = MaterialTheme.colorScheme.secondary,
				errorLabelColor = MaterialTheme.colorScheme.error,
			),
			modifier = Modifier
				.padding(horizontal = 16.dp)
				.width(400.dp)
				.onFocusChanged {
					emailHasFocus = it.hasFocus
				},
		)
		emailError?.let {
			Text(
				text = it,
				style = TextStyle(
					fontFamily = montserrat,
					fontSize = Typography.bodyLarge.fontSize * tsf,
					color = MaterialTheme.colorScheme.error,
				),
				modifier = Modifier
					.fillMaxWidth()
					.padding(
						top = 5.dp,
						start = 16.dp,
						end = 16.dp,
						bottom = 0.dp,
					)
					.align(Alignment.Start)
			)
		}
		Spacer(modifier = Modifier.height((emailError?.let { 0 } ?: 8).dp))
		OutlinedTextField(
			value = password,
			onValueChange = {
				password = it
				passwordError = null
			},
			textStyle = TextStyle(
				fontSize = Typography.bodyLarge.fontSize * tsf,
				fontFamily = montserrat
			),
			isError = passwordError != null,
			singleLine = true,
			visualTransformation =
			if (showPassword)
				VisualTransformation.None
			else
				PasswordVisualTransformation(),
			keyboardOptions = KeyboardOptions(
				keyboardType = KeyboardType.Password,
			),
			label = {
				Text(
					stringResource(id = R.string.enter_password),
					fontSize = Typography.bodyLarge.fontSize * tsf,
				)
			},
			placeholder = {
				Text(
					stringResource(id = R.string.enter_password),
					fontSize = Typography.bodyLarge.fontSize * tsf,
				)
			},
			leadingIcon = {
				Icon(
					imageVector = Icons.Default.Lock,
					contentDescription = null,
					tint =
					if (passwordError != null)
						MaterialTheme.colorScheme.error
					else if (passwordHasFocus)
						MaterialTheme.colorScheme.secondary
					else
						Color.Gray,
				)
			},
			trailingIcon = {
				IconButton(
					onClick = {
						showPassword = !showPassword
					},
				) {
					Icon(
						painter = painterResource(id = if (showPassword) R.drawable.ic_visibility else R.drawable.ic_visibility_off),
						contentDescription = null,
						tint =
						if (passwordError != null)
							MaterialTheme.colorScheme.error
						else if (passwordHasFocus)
							MaterialTheme.colorScheme.secondary
						else
							Color.Gray,
					)
				}
			},
			colors = OutlinedTextFieldDefaults.colors(
				cursorColor = MaterialTheme.colorScheme.secondary,
				errorCursorColor = MaterialTheme.colorScheme.error,
				focusedBorderColor = MaterialTheme.colorScheme.secondary,
				errorBorderColor = MaterialTheme.colorScheme.error,
				focusedLabelColor = MaterialTheme.colorScheme.secondary,
				errorLabelColor = MaterialTheme.colorScheme.error,
			),
			modifier = Modifier
				.padding(horizontal = 16.dp)
				.width(400.dp)
				.onFocusChanged {
					passwordHasFocus = it.hasFocus
				},
		)
		passwordError?.let {
			Text(
				text = it,
				style = TextStyle(
					fontFamily = montserrat,
					color = MaterialTheme.colorScheme.error,
					fontSize = Typography.bodyLarge.fontSize * tsf,
				),
				modifier = Modifier
					.fillMaxWidth()
					.padding(
						top = 5.dp,
						start = 16.dp,
						end = 16.dp,
						bottom = 0.dp,
					)
					.align(Alignment.Start)
			)
		}
		Spacer(modifier = Modifier.height((passwordError?.let { 0 } ?: 8).dp))
		ElevatedButton(
			onClick = {
				coroutineScope.launch {
					loading = true
					val res = httpClient!!
						.requestFromAPI("login", HttpMethod.Post, LoginBody(email, password))
					when (res.status) {
						HttpStatusCode.OK -> {
							viewModel.setUser(res.body<UserResponse>().payload!!)
							navController.navigate(Page.Home.route) {
								popUpTo(Page.Auth.route) { inclusive = true }
							}
						}
						
						HttpStatusCode.NotFound -> {
							email = ""
							password = ""
							emailError = context.resources.getString(R.string.user_not_found)
						}
						
						HttpStatusCode.NotAcceptable -> {
							if (res.body<EmptyResponse>().message == "This account is disabled") {
								email = ""
								password = ""
								emailError = context.resources.getString(R.string.account_disabled)
							} else if (res.body<EmptyResponse>().message == "Incorrect password") {
								password = ""
								passwordError = context.resources.getString(R.string.incorrect_password)
							}
						}
						
						HttpStatusCode.InternalServerError -> {
							Toast.makeText(context, R.string.login_again, Toast.LENGTH_SHORT).show()
						}
					}
					loading = false
				}
			},
			colors = ButtonDefaults.elevatedButtonColors(
				containerColor = colorResource(id = R.color.primary),
			)
		) {
			Text(
				text = stringResource(id = R.string.login),
				style = TextStyle(
					fontFamily = montserrat,
					color = Color.Black,
					fontSize = 15.sp * tsf,
					fontWeight = FontWeight.Bold,
				)
			)
		}
		Spacer(modifier = Modifier.height(8.dp))
		Text(
			text = stringResource(R.string.sign_up_instead),
			style = TextStyle(
				fontFamily = montserrat,
				textDecoration = TextDecoration.Underline,
				color = MaterialTheme.colorScheme.scrim,
				fontSize = Typography.bodyLarge.fontSize * tsf,
			),
			modifier = Modifier
				.clickable {
					changeTab()
				}
		)
		Spacer(modifier = Modifier.height(8.dp))
		if (loading) CircularProgressIndicator(color = MaterialTheme.colorScheme.secondary)
	}
}
