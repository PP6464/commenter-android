package com.chat.commenter.compose.elements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.chat.commenter.state.AppViewModel
import com.chat.commenter.ui.theme.Typography
import com.chat.commenter.ui.theme.montserrat
import org.koin.androidx.compose.koinViewModel

@Composable
fun MyTextField(
	value: String,
	onValueChange: (String) -> Unit,
	onFocusChanged: (FocusState) -> Unit,
	hasFocus: Boolean,
	leadingIcon: ImageVector? = null,
	trailingIcon: ImageVector? = null,
	trailingIconOnPress: (() -> Unit) = {},
	obscureText: Boolean = false,
	error: String? = null,
	placeholder: String? = null,
	charLimit: Int? = null,
	singleLine: Boolean = true,
	keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
	supportingText: String? = null,
	viewModel: AppViewModel = koinViewModel(),
) {
	val ui by viewModel.uiState.collectAsState()
	val tsf = ui.tsf
	
	Column(
		horizontalAlignment = Alignment.CenterHorizontally,
		modifier = Modifier
			.width(500.dp)
			.padding(horizontal = 16.dp)
	) {
		OutlinedTextField(
			value = value,
			onValueChange = onValueChange,
			leadingIcon =
				leadingIcon?.let {
					{
						Icon(
							imageVector = it,
							contentDescription = null,
							tint =
								if (error != null)
									MaterialTheme.colorScheme.error
								else if (hasFocus)
									MaterialTheme.colorScheme.secondary
								else
									Color.Gray,
						)
					}
				},
			trailingIcon =
				trailingIcon?.let {
					{
						IconButton(
							onClick = trailingIconOnPress,
						) {
							Icon(
								imageVector = it,
								contentDescription = null,
								tint =
									if (error != null)
										MaterialTheme.colorScheme.error
									else if (hasFocus)
										MaterialTheme.colorScheme.secondary
									else
										Color.Gray,
							)
						}
					}
				},
			isError = error != null,
			singleLine = singleLine,
			textStyle = TextStyle(
				fontFamily = montserrat,
				fontSize = Typography.bodyLarge.fontSize * tsf,
			),
			keyboardOptions = keyboardOptions,
			visualTransformation = if (obscureText)
				PasswordVisualTransformation()
			else
				VisualTransformation.None,
			placeholder = {
				if (placeholder != null) {
					Text(
						placeholder,
						fontFamily = montserrat,
						fontSize = Typography.bodyLarge.fontSize * tsf,
					)
				}
			},
			label = {
				if (placeholder != null) {
					Text(
						placeholder,
						fontFamily = montserrat,
						fontSize = Typography.bodyLarge.fontSize * tsf,
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
				.width(500.dp)
				.onFocusChanged(onFocusChanged)
		)
		charLimit?.let { c ->
			error?.let {
				Row(
					horizontalArrangement = Arrangement.SpaceBetween,
					modifier = Modifier
						.fillMaxWidth()
						.padding(top = 5.dp)
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
						text = "${value.length}/$c",
						style = TextStyle(
							fontFamily = montserrat,
							color = MaterialTheme.colorScheme.error,
							fontSize = Typography.bodyLarge.fontSize * tsf,
						),
					)
				}
			} ?: Text(
				text = "${value.length}/$c",
				style = TextStyle(
					fontFamily = montserrat,
					fontSize = Typography.bodyLarge.fontSize * tsf,
				),
				modifier = Modifier
					.align(Alignment.End)
					.padding(top = 5.dp)
			)
		} ?: error?.let {
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
						bottom = 0.dp,
					)
					.align(Alignment.Start)
			)
		} ?: supportingText?.let {
			Text(
				text = it,
				style = TextStyle(
					fontFamily = montserrat,
					fontSize = Typography.bodyLarge.fontSize * tsf,
				),
				modifier = Modifier
					.fillMaxWidth()
					.padding(
						top = 5.dp,
						bottom = 0.dp,
					)
					.align(Alignment.Start)
			)
		}
	}
}

@Composable
fun MyTextField(
	value: String,
	onValueChange: (String) -> Unit,
	onFocusChanged: (FocusState) -> Unit,
	hasFocus: Boolean,
	leadingIcon: ImageVector? = null,
	trailingIcon: Painter,
	trailingIconOnPress: (() -> Unit) = {},
	obscureText: Boolean = false,
	error: String? = null,
	placeholder: String? = null,
	charLimit: Int? = null,
	singleLine: Boolean = true,
	keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
	supportingText: String? = null,
	viewModel: AppViewModel = koinViewModel(),
) {
	val ui by viewModel.uiState.collectAsState()
	val tsf = ui.tsf
	
	Column(
		horizontalAlignment = Alignment.CenterHorizontally,
		modifier = Modifier
			.width(500.dp)
			.padding(horizontal = 16.dp)
	) {
		OutlinedTextField(
			value = value,
			onValueChange = onValueChange,
			leadingIcon =
				leadingIcon?.let {
					{
						Icon(
							imageVector = it,
							contentDescription = null,
							tint =
								if (error != null)
									MaterialTheme.colorScheme.error
								else if (hasFocus)
									MaterialTheme.colorScheme.secondary
								else
									Color.Gray,
						)
					}
				},
			trailingIcon = {
				IconButton(
					onClick = trailingIconOnPress,
				) {
					Icon(
						painter = trailingIcon,
						contentDescription = null,
					)
				}
			},
			isError = error != null,
			singleLine = singleLine,
			textStyle = TextStyle(
				fontFamily = montserrat,
				fontSize = Typography.bodyLarge.fontSize * tsf,
			),
			keyboardOptions = keyboardOptions,
			visualTransformation = if (obscureText)
				PasswordVisualTransformation()
			else
				VisualTransformation.None,
			placeholder = {
				if (placeholder != null) {
					Text(
						placeholder,
						fontFamily = montserrat,
						fontSize = Typography.bodyLarge.fontSize * tsf,
					)
				}
			},
			label = {
				if (placeholder != null) {
					Text(
						placeholder,
						fontFamily = montserrat,
						fontSize = Typography.bodyLarge.fontSize * tsf,
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
				.width(500.dp)
				.onFocusChanged(onFocusChanged)
		)
		charLimit?.let { c ->
			error?.let {
				Row(
					horizontalArrangement = Arrangement.SpaceBetween,
					modifier = Modifier
						.fillMaxWidth()
						.padding(top = 5.dp)
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
						text = "${value.length}/$c",
						style = TextStyle(
							fontFamily = montserrat,
							color = MaterialTheme.colorScheme.error,
							fontSize = Typography.bodyLarge.fontSize * tsf,
						),
					)
				}
			} ?: Text(
				text = "${value.length}/$c",
				style = TextStyle(
					fontFamily = montserrat,
					fontSize = Typography.bodyLarge.fontSize * tsf,
				),
				modifier = Modifier
					.align(Alignment.End)
					.padding(top = 5.dp)
			)
		} ?: error?.let {
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
						bottom = 0.dp,
					)
					.align(Alignment.Start)
			)
		} ?: supportingText?.let {
			Text(
				text = it,
				style = TextStyle(
					fontFamily = montserrat,
					fontSize = Typography.bodyLarge.fontSize * tsf,
				),
				modifier = Modifier
					.fillMaxWidth()
					.padding(
						top = 5.dp,
						bottom = 0.dp,
					)
					.align(Alignment.Start)
			)
		}
	}
}