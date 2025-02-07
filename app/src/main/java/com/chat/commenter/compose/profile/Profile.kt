package com.chat.commenter.compose.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import com.chat.commenter.Page
import com.chat.commenter.R
import com.chat.commenter.compose.elements.AppBar
import com.chat.commenter.state.AppViewModel
import com.chat.commenter.ui.theme.Typography
import com.chat.commenter.ui.theme.montserrat
import org.koin.androidx.compose.koinViewModel

sealed class PicSources(val name: String, open val source : String) {
	data class File(override val source : String) : PicSources("file", source)
	data class URL(override val source: String) : PicSources("url", source)
}

@Composable
fun Profile(
	viewModel: AppViewModel = koinViewModel(),
) {
	val userState by viewModel.userState.collectAsState()
	val navController = rememberNavController()
	val user = userState.user!!
	val ui by viewModel.uiState.collectAsState()
	val tsf = ui.tsf
	// Inputs
	var displayName by remember { mutableStateOf(user.displayName) }
	var status by remember { mutableStateOf(user.status) }
	var email by remember { mutableStateOf(user.email) }
	var password by remember { mutableStateOf("") }
	var picSource by remember { mutableStateOf(PicSources.URL(user.pic)) }
	// Errors
	var displayNameError by remember { mutableStateOf<String?>(null) }
	var statusError by remember { mutableStateOf<String?>(null) }
	var emailError by remember { mutableStateOf<String?>(null) }
	var passwordError by remember { mutableStateOf<String?>(null) }
	// UI
	var loading by remember { mutableStateOf(false) }
	var showPassword by remember { mutableStateOf(false) }
	var emailHasFocus by remember { mutableStateOf(false) }
	var passwordHasFocus by remember { mutableStateOf(false) }
	var displayNameHasFocus by remember { mutableStateOf(false) }
	var statusHasFocus by remember { mutableStateOf(false) }
	// Coroutines
	val coroutineScope = rememberCoroutineScope()
	
	Scaffold(
		topBar = {
			AppBar(
				pageToHide = Page.Profile,
			)
		},
	) { padding ->
		Column(
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.Center,
			modifier = Modifier
				.fillMaxSize()
				.padding(padding)
		) {
			AsyncImage(
				model = user.pic,
				contentDescription = null,
				modifier = Modifier
					.padding(16.dp)
					.size(200.dp)
					.clip(CircleShape),
			)
			Text(
				stringResource(R.string.profile),
				fontFamily = montserrat,
				fontSize = 30.sp * tsf,
				fontWeight = FontWeight.Bold,
			)
			Spacer(modifier = Modifier.height(8.dp))
			OutlinedTextField(
				value = displayName,
				onValueChange = {
					displayName = it.take(20)
					displayNameError = null
				},
				textStyle = TextStyle(
					fontFamily = montserrat,
					fontSize = Typography.bodyLarge.fontSize * tsf,
				),
				isError = displayNameError != null,
				singleLine = true,
				label = {
					Text(
						stringResource(id = R.string.enter_display_name),
						fontSize = Typography.bodyLarge.fontSize * tsf,
						fontFamily = montserrat,
					)
				},
				placeholder = {
					Text(
						stringResource(id = R.string.enter_display_name),
						fontSize = Typography.bodyLarge.fontSize * tsf,
					)
				},
				leadingIcon = {
					Icon(
						imageVector = Icons.Default.Person,
						contentDescription = null,
						tint =
							if (displayNameError != null)
								MaterialTheme.colorScheme.error
							else if (displayNameHasFocus)
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
						displayNameHasFocus = it.hasFocus
					},
			)
			displayNameError?.let {
				Row(
					horizontalArrangement = Arrangement.SpaceBetween,
					modifier = Modifier
						.fillMaxWidth()
						.padding(
							top = 5.dp,
							start = 16.dp,
							end = 16.dp,
						)
				) {
					Text(
						text = it,
						style = TextStyle(
							fontFamily = montserrat,
							color = MaterialTheme.colorScheme.error,
							fontSize = Typography.bodyLarge.fontSize * tsf,
						),
					)
					Text(
						text = "${displayName.length}/20",
						style = TextStyle(
							fontFamily = montserrat,
							color = MaterialTheme.colorScheme.error,
							fontSize = Typography.bodyLarge.fontSize * tsf,
						),
					)
				}
			} ?: Text(
				text = "${displayName.length}/20",
				style = TextStyle(
					fontFamily = montserrat,
					fontSize = Typography.bodyLarge.fontSize * tsf,
				),
				modifier = Modifier
					.align(Alignment.End)
					.padding(
						top = 5.dp,
						start = 16.dp,
						end = 16.dp,
					)
			)
			OutlinedTextField(
				value = status,
				onValueChange = {
					status = it.take(50)
					statusError = null
				},
				textStyle = TextStyle(
					fontFamily = montserrat,
					fontSize = Typography.bodyLarge.fontSize * tsf,
				),
				isError = statusError != null,
				singleLine = true,
				label = {
					Text(
						stringResource(id = R.string.enter_status),
						fontSize = Typography.bodyLarge.fontSize * tsf,
						fontFamily = montserrat,
					)
				},
				placeholder = {
					Text(
						stringResource(id = R.string.enter_status),
						fontSize = Typography.bodyLarge.fontSize * tsf,
					)
				},
				leadingIcon = {
					Icon(
						imageVector = Icons.Default.AccountCircle,
						contentDescription = null,
						tint =
							if (statusError != null)
								MaterialTheme.colorScheme.error
							else if (statusHasFocus)
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
						statusHasFocus = it.hasFocus
					},
			)
			statusError?.let {
				Row(
					horizontalArrangement = Arrangement.SpaceBetween,
					modifier = Modifier
						.fillMaxWidth()
						.padding(
							top = 5.dp,
							start = 16.dp,
							end = 16.dp,
						)
				) {
					Text(
						text = it,
						style = TextStyle(
							fontFamily = montserrat,
							color = MaterialTheme.colorScheme.error,
							fontSize = Typography.bodyLarge.fontSize * tsf,
						),
					)
					Text(
						text = "${status.length}/50",
						style = TextStyle(
							fontFamily = montserrat,
							color = MaterialTheme.colorScheme.error,
							fontSize = Typography.bodyLarge.fontSize * tsf,
						),
					)
				}
			} ?: Text(
				text = "${status.length}/50",
				style = TextStyle(
					fontFamily = montserrat,
					fontSize = Typography.bodyLarge.fontSize * tsf,
				),
				modifier = Modifier
					.align(Alignment.End)
					.padding(
						top = 5.dp,
						start = 16.dp,
						end = 16.dp,
					)
			)
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
			Text(
				text = passwordError ?: stringResource(R.string.leave_password_empty),
				style = TextStyle(
					fontFamily = montserrat,
					color = if (passwordError != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onPrimary,
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
			ElevatedButton(
				onClick = {
				},
				colors = ButtonDefaults.elevatedButtonColors(
					containerColor = colorResource(id = R.color.primary),
				)
			) {
				Text(
					text = stringResource(id = R.string.save_changes),
					style = TextStyle(
						fontFamily = montserrat,
						color = Color.Black,
						fontSize = 15.sp * tsf,
						fontWeight = FontWeight.Bold,
					)
				)
			}
			Spacer(modifier = Modifier.height(8.dp))
			if (loading) CircularProgressIndicator(color = MaterialTheme.colorScheme.secondary)
		}
	}
}