package com.chat.commenter.compose.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.chat.commenter.R
import com.chat.commenter.state.AppViewModel
import com.chat.commenter.ui.theme.Typography
import com.chat.commenter.ui.theme.montserrat
import org.koin.androidx.compose.koinViewModel

@Composable
fun SignUp(
	navController: NavController,
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
	val displayNameMaxLength = 20
	// String resources
	val displayNameMaxLengthError =
		stringResource(id = R.string.display_name_max_length, displayNameMaxLength)
	val emailFormatError = stringResource(id = R.string.email_formatted_incorrectly)
	val passwordLength = stringResource(id = R.string.password_length)
	// UI
	val tsf = viewModel.getTSF()
	
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
		OutlinedTextField(
			value = displayName,
			onValueChange = {
				displayName = it.take(displayNameMaxLength)
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
					text = "${displayName.length}/${displayNameMaxLength}",
					style = TextStyle(
						fontFamily = montserrat,
						color = MaterialTheme.colorScheme.error,
						fontSize = Typography.bodyLarge.fontSize * tsf,
					),
				)
			}
		} ?: Text(
			text = "${displayName.length}/${displayNameMaxLength}",
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
				fontFamily = montserrat,
				fontSize = Typography.bodyLarge.fontSize * tsf,
			),
			isError = emailError != null,
			singleLine = true,
			label = {
				Text(
					stringResource(id = R.string.enter_email),
					fontSize = Typography.bodyLarge.fontSize * tsf,
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
					color = MaterialTheme.colorScheme.error,
					fontSize = Typography.bodyLarge.fontSize * tsf,
				),
				modifier = Modifier
					.fillMaxWidth()
					.padding(
						top = 5.dp,
						start = 16.dp,
						end = 16.dp,
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
				fontFamily = montserrat,
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
					)
					.align(Alignment.Start)
			)
		}
		Spacer(modifier = Modifier.height((passwordError?.let { 0 } ?: 8).dp))
		ElevatedButton(
			onClick = {
				displayNameError =
					if (displayName.length < displayNameMaxLength) displayNameMaxLengthError else null
				emailError = if (emailError == null) "Error" else null
				passwordError = if (passwordError == null) "Error" else null
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
	}
}