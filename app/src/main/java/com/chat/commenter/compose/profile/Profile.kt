package com.chat.commenter.compose.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import com.chat.commenter.Page
import com.chat.commenter.R
import com.chat.commenter.compose.elements.AppBar
import com.chat.commenter.compose.elements.MyTextField
import com.chat.commenter.state.AppViewModel
import com.chat.commenter.ui.theme.montserrat
import org.koin.androidx.compose.koinViewModel

sealed class PicSources(val name: String, open val source: String) {
	data class File(override val source: String) : PicSources("file", source)
	data class URL(override val source: String) : PicSources("url", source)
}

@Composable
fun Profile(
	viewModel: AppViewModel = koinViewModel(),
) {
	val userState by viewModel.userState.collectAsState()
	val navController = rememberNavController()
	val user = userState.user
	val ui by viewModel.uiState.collectAsState()
	val tsf = ui.tsf
	// Inputs
	var displayName by remember { mutableStateOf(user?.displayName ?: "") }
	var status by remember { mutableStateOf(user?.status ?: "") }
	var email by remember { mutableStateOf(user?.email ?: "") }
	var password by remember { mutableStateOf("") }
	var picSource by remember { mutableStateOf(user?.pic?.let { PicSources.URL(it) }) }
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
	
	if (user != null) {
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
					.verticalScroll(rememberScrollState())
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
				MyTextField(
					value = displayName,
					onValueChange = {
						displayName = it.take(20)
						displayNameError = null
					},
					onFocusChanged = {
						displayNameHasFocus = it.hasFocus
					},
					leadingIcon = Icons.Default.Person,
					hasFocus = displayNameHasFocus,
					error = displayNameError,
					placeholder = stringResource(R.string.enter_new_display_name),
					charLimit = 20,
				)
				MyTextField(
					value = status,
					onValueChange = {
						status = it.take(50)
						statusError = null
					},
					onFocusChanged = {
						statusHasFocus = it.hasFocus
					},
					leadingIcon = Icons.Default.AccountCircle,
					hasFocus = statusHasFocus,
					error = statusError,
					placeholder = stringResource(R.string.enter_new_status),
					charLimit = 50,
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
					leadingIcon = Icons.Default.Email,
					hasFocus = emailHasFocus,
					error = emailError,
					keyboardOptions = KeyboardOptions(
						keyboardType = KeyboardType.Email,
					),
					placeholder = stringResource(R.string.enter_new_email),
				)
				Spacer(modifier = Modifier.height((emailError?.let { 0 } ?: 8).dp))
				MyTextField(
					value = password,
					onValueChange = {
						password = it
						passwordError = null
					},
					leadingIcon = Icons.Default.Lock,
					trailingIcon = if (showPassword) painterResource(R.drawable.ic_visibility) else painterResource(
						R.drawable.ic_visibility_off
					),
					trailingIconOnPress = { showPassword = !showPassword },
					obscureText = !showPassword,
					hasFocus = passwordHasFocus,
					onFocusChanged = {
						passwordHasFocus = it.hasFocus
					},
					error = passwordError,
					placeholder = stringResource(R.string.enter_new_password),
					keyboardOptions = KeyboardOptions(
						keyboardType = KeyboardType.Password,
					),
					supportingText = stringResource(R.string.leave_password_empty),
				)
				Spacer(modifier = Modifier.height(8.dp))
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
}