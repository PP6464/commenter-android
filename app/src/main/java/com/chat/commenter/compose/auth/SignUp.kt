package com.chat.commenter.compose.auth

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chat.commenter.LocalNavController
import com.chat.commenter.Page
import com.chat.commenter.R
import com.chat.commenter.api.EmptyResponse
import com.chat.commenter.api.SignUpBody
import com.chat.commenter.api.UserResponse
import com.chat.commenter.api.requestFromAPI
import com.chat.commenter.compose.elements.MyTextField
import com.chat.commenter.state.AppViewModel
import com.chat.commenter.ui.theme.Typography
import com.chat.commenter.ui.theme.montserrat
import io.ktor.client.call.body
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SignUp(
	viewModel: AppViewModel = koinViewModel(),
	changeTab: () -> Unit,
) {
	// Text values
	var email by remember { mutableStateOf("") }
	var password by remember { mutableStateOf("") }
	var displayName by remember { mutableStateOf("") }
	// Show/Hide password
	var showPassword by remember { mutableStateOf(false) }
	// Check if text fields have focus
	var displayNameHasFocus by remember { mutableStateOf(false) }
	var emailHasFocus by remember { mutableStateOf(false) }
	var passwordHasFocus by remember { mutableStateOf(false) }
	// Error messages for each text field
	var displayNameError by remember { mutableStateOf<String?>(null) }
	var emailError by remember { mutableStateOf<String?>(null) }
	var passwordError by remember { mutableStateOf<String?>(null) }
	// Coroutines
	val coroutineScope = rememberCoroutineScope()
	// UI
	val ui by viewModel.uiState.collectAsState()
	val tsf = ui.tsf
	var loading by remember { mutableStateOf(false) }
	val loadingBringIntoViewRequester = remember { BringIntoViewRequester() }
	val context = LocalContext.current
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
			text = stringResource(id = R.string.sign_up),
			style = TextStyle(
				fontWeight = FontWeight.Bold,
				fontSize = 30.sp * tsf,
				fontFamily = montserrat,
			)
		)
		Spacer(modifier = Modifier.height(8.dp))
		MyTextField(
			value = displayName,
			onValueChange = {
				displayName = it
				displayNameError = null
			},
			hasFocus = displayNameHasFocus,
			onFocusChanged = {
				displayNameHasFocus = it.hasFocus
			},
			error = displayNameError,
			charLimit = 20,
			leadingIcon = Icons.Default.Person,
			placeholder = stringResource(R.string.enter_display_name),
		)
		MyTextField(
			value = email,
			onValueChange = {
				email = it
				emailError = null
			},
			onFocusChanged = {
				emailHasFocus = it.hasFocus
			},
			hasFocus = emailHasFocus,
			leadingIcon = Icons.Default.Email,
			keyboardOptions = KeyboardOptions(
				keyboardType = KeyboardType.Email,
			),
			placeholder = stringResource(R.string.enter_email),
			error = emailError,
		)
		Spacer(modifier = Modifier.height((emailError?.let { 0 } ?: 8).dp))
		MyTextField(
			value = password,
			onValueChange = {
				password = it
				passwordError = null
			},
			hasFocus = passwordHasFocus,
			onFocusChanged = {
				passwordHasFocus = it.hasFocus
			},
			supportingText = stringResource(R.string.password_length_shortened),
			leadingIcon = Icons.Default.Lock,
			trailingIcon = if (showPassword) painterResource(R.drawable.ic_visibility) else painterResource(R.drawable.ic_visibility_off),
			trailingIconOnPress = { showPassword = !showPassword },
			error = passwordError,
			obscureText = !showPassword,
			keyboardOptions = KeyboardOptions(
				keyboardType = KeyboardType.Password,
			),
			placeholder = stringResource(R.string.enter_password),
		)
		Spacer(modifier = Modifier.height((passwordError?.let { 0 } ?: 8).dp))
		ElevatedButton(
			onClick = {
				coroutineScope.launch {
					loading = true
					loadingBringIntoViewRequester.bringIntoView()
					val res = httpClient!!.requestFromAPI(
						path = "sign-up",
						method = HttpMethod.Post,
						SignUpBody(
							displayName = displayName,
							email = email,
							password = password,
						),
					)
					
					when (res.status) {
						HttpStatusCode.Created -> {
							viewModel.setUser(res.body<UserResponse>().payload!!)
							navController.navigate(Page.Home.route) {
								popUpTo(Page.Auth.route) { inclusive = true }
							}
						}
						
						HttpStatusCode.NotAcceptable -> {
							when (res.body<EmptyResponse>().message) {
								"Password is too short" -> {
									passwordError = context.resources.getString(R.string.password_length)
								}
								
								"Display name is too long" -> {
									displayName = ""
									displayNameError = context.resources.getString(R.string.display_name_max_length)
								}
								
								"Display name cannot be empty" -> {
									displayNameError = context.resources.getString(R.string.display_name_empty)
								}
								
								"Email is invalid" -> {
									email = ""
									emailError = context.resources.getString(R.string.email_formatted_incorrectly)
								}
							}
						}
						
						HttpStatusCode.Conflict -> {
							email = ""
							emailError = context.resources.getString(R.string.email_in_use)
						}
						
						HttpStatusCode.InternalServerError -> {
							Toast.makeText(context, R.string.sign_up_again, Toast.LENGTH_SHORT).show()
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
				text = stringResource(id = R.string.sign_up),
				style = TextStyle(
					fontFamily = montserrat,
					fontSize = 15.sp * tsf,
					color = Color.Black,
					fontWeight = FontWeight.Bold
				)
			)
		}
		Spacer(modifier = Modifier.height(8.dp))
		Text(
			text = stringResource(id = R.string.already_have_account),
			style = TextStyle(
				fontFamily = montserrat,
				color = MaterialTheme.colorScheme.scrim,
				textDecoration = TextDecoration.Underline,
				fontSize = Typography.bodyLarge.fontSize * tsf,
			),
			modifier = Modifier
				.clickable {
					changeTab()
				}
		)
		Spacer(modifier = Modifier.height(8.dp))
		if (loading) {
			CircularProgressIndicator(
				color = MaterialTheme.colorScheme.secondary,
				modifier = Modifier
					.bringIntoViewRequester(loadingBringIntoViewRequester)
					.padding(bottom = 16.dp)
			)
		}
	}
}