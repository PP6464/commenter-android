package com.chat.commenter.compose.profile

import android.Manifest
import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import com.chat.commenter.Page
import com.chat.commenter.R
import com.chat.commenter.api.EmptyResponse
import com.chat.commenter.api.ProfileUpdateBody
import com.chat.commenter.api.UserResponse
import com.chat.commenter.api.checkImageURL
import com.chat.commenter.api.requestMultipartFromAPI
import com.chat.commenter.compose.elements.AppBar
import com.chat.commenter.compose.elements.MyTextField
import com.chat.commenter.permissions.rememberPermissionRequester
import com.chat.commenter.state.AppViewModel
import com.chat.commenter.ui.theme.Typography
import com.chat.commenter.ui.theme.montserrat
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import io.ktor.client.call.body
import io.ktor.client.request.forms.formData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.encodeURLPathPart
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import network.chaintech.cmpimagepickncrop.CMPImagePickNCropDialog
import network.chaintech.cmpimagepickncrop.imagecropper.CircleImgCropShape
import network.chaintech.cmpimagepickncrop.imagecropper.ImageAspectRatio
import network.chaintech.cmpimagepickncrop.imagecropper.rememberImageCropper
import org.koin.androidx.compose.koinViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 0: No Change
 * 1: URL
 * 2: Initials
 * 3: Gallery/Camera (Picture)
 */
sealed class ProfilePicSources(val nameIndex: Int, val type: String, open val source: String) {
	data class NoChange(override val source: String) : ProfilePicSources(0, "url", source)
	data class URL(override val source: String) : ProfilePicSources(1, "url", source)
	data class Initials(override val source: String) : ProfilePicSources(2, "url", source)
	data class Picture(override val source: String) : ProfilePicSources(3, "file", source)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun Profile(
	viewModel: AppViewModel = koinViewModel(),
) {
	// State & navigation
	val userState by viewModel.userState.collectAsState()
	val user = userState.user
	val ui by viewModel.uiState.collectAsState()
	val tsf = ui.tsf
	val client by viewModel.clientState.collectAsState()
	// Inputs
	var displayName by remember { mutableStateOf(user?.displayName ?: "") }
	var status by remember { mutableStateOf(user?.status ?: "") }
	var email by remember { mutableStateOf(user?.email ?: "") }
	var password by remember { mutableStateOf("") }
	var picSource by remember {
		mutableStateOf<ProfilePicSources?>(user?.pic?.let {
			ProfilePicSources.NoChange(it)
		})
	}
	var picURL by remember { mutableStateOf("") }
	// Errors
	var displayNameError by remember { mutableStateOf<String?>(null) }
	var statusError by remember { mutableStateOf<String?>(null) }
	var emailError by remember { mutableStateOf<String?>(null) }
	var passwordError by remember { mutableStateOf<String?>(null) }
	// UI
	val context = LocalContext.current
	var loading by remember { mutableStateOf(false) }
	val loadingBringIntoViewRequester = remember { BringIntoViewRequester() }
	var showPassword by remember { mutableStateOf(false) }
	var emailHasFocus by remember { mutableStateOf(false) }
	var passwordHasFocus by remember { mutableStateOf(false) }
	var displayNameHasFocus by remember { mutableStateOf(false) }
	var statusHasFocus by remember { mutableStateOf(false) }
	var expanded by remember { mutableStateOf(false) }
	var urlPopupShowing by remember { mutableStateOf(false) }
	// Coroutines
	val coroutineScope = rememberCoroutineScope()
	// Image picking
	var openImagePicker by remember { mutableStateOf(false) }
	var imageBitmap: ImageBitmap? by remember { mutableStateOf(null) }
	val imageCropper = rememberImageCropper()
	// Colour picker
	val colourPickerController = rememberColorPickerController()
	var pickingColour by remember { mutableStateOf(false) }
	var colourPicked by remember { mutableStateOf("") }
	// Permissions
	val requestCameraPermission = rememberPermissionRequester(permission = Manifest.permission.CAMERA)
	
	if (user != null) {
		Scaffold(
			topBar = {
				AppBar(
					currentPage = Page.Profile,
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
				CMPImagePickNCropDialog(
					imageCropper = imageCropper,
					shapes = listOf(CircleImgCropShape),
					aspects = listOf(ImageAspectRatio(1, 1)),
					openImagePicker = openImagePicker,
					imagePickerDialogHandler = {
						openImagePicker = it
					},
					selectedImageCallback = {
						imageBitmap = it
						picSource = ProfilePicSources.Picture(it.toString())
					}
				)
				if (pickingColour) {
					AlertDialog(
						onDismissRequest = { pickingColour = false },
						text = {
							Column(
								horizontalAlignment = Alignment.Start,
							) {
								Text(
									text = stringResource(R.string.pick_colour_title),
									style = TextStyle(
										fontWeight = FontWeight.Bold,
										fontSize = Typography.bodyLarge.fontSize * tsf * 1.5,
										fontFamily = montserrat,
									),
								)
								Text(
									text = stringResource(R.string.pick_colour_initials),
									style = TextStyle(
										fontFamily = montserrat,
										fontSize = Typography.bodyLarge.fontSize * tsf,
									)
								)
								HsvColorPicker(
									controller = colourPickerController,
									onColorChanged = {
										colourPicked = it.hexCode
									},
									modifier = Modifier
										.size(200.dp)
										.padding(16.dp)
										.align(Alignment.CenterHorizontally)
								)
							}
						},
						dismissButton = {
							TextButton(
								onClick = {
									pickingColour = false
								}
							) {
								Text(
									text = stringResource(R.string.cancel),
									color = MaterialTheme.colorScheme.error,
									fontFamily = montserrat,
									fontSize = Typography.bodyLarge.fontSize * tsf,
								)
							}
						},
						confirmButton = {
							TextButton(
								onClick = {
									picSource = ProfilePicSources.Initials("https://eu.ui-avatars.com/api/?name=${displayName.encodeURLPathPart()}&size=128&rounded=true&background=${colourPicked.drop(2)}")
									pickingColour = false
								}
							) {
								Text(
									text = stringResource(R.string.confirm),
									color = MaterialTheme.colorScheme.tertiary,
									fontFamily = montserrat,
									fontSize = Typography.bodyLarge.fontSize * tsf,
								)
							}
						},
					)
				}
				if (urlPopupShowing) {
					AlertDialog(
						onDismissRequest = { urlPopupShowing = false },
						text = {
							MyTextField(
								value = picURL,
								onValueChange = { picURL = it },
								placeholder = stringResource(R.string.enter_url),
								hasFocus = true,
								onFocusChanged = {},
							)
						},
						dismissButton = {
							TextButton(
								onClick = {
									urlPopupShowing = false
								}
							) {
								Text(
									text = stringResource(R.string.cancel),
									color = MaterialTheme.colorScheme.error,
									fontFamily = montserrat,
									fontSize = Typography.bodyLarge.fontSize * tsf,
								)
							}
						},
						confirmButton = {
							TextButton(
								onClick = {
									coroutineScope.launch {
										val isImage = client!!.checkImageURL(picURL)
										if (isImage) {
											picSource = ProfilePicSources.URL(picURL)
											urlPopupShowing = false
										} else {
											Toast.makeText(
												context,
												context.resources.getString(R.string.invalid_image_url),
												Toast.LENGTH_SHORT,
											).show()
										}
									}
								}
							) {
								Text(
									text = stringResource(R.string.confirm),
									color = MaterialTheme.colorScheme.tertiary,
									fontFamily = montserrat,
									fontSize = Typography.bodyLarge.fontSize * tsf,
								)
							}
						},
					)
				}
				if (picSource!!.type == "url") {
					AsyncImage(
						model = ImageRequest.Builder(context)
							.data(picSource!!.source)
							.diskCachePolicy(CachePolicy.DISABLED)
							.memoryCachePolicy(CachePolicy.DISABLED)
							.build(),
						contentDescription = null,
						modifier = Modifier
							.padding(16.dp)
							.size(200.dp)
							.clip(CircleShape),
					)
				} else if (imageBitmap != null) {
					Image(
						bitmap = imageBitmap!!,
						contentDescription = null,
						modifier = Modifier
							.padding(16.dp)
							.clip(CircleShape)
							.size(200.dp)
					)
				}
				ExposedDropdownMenuBox(
					expanded = expanded,
					onExpandedChange = { expanded = it },
				) {
					TextField(
						value = stringArrayResource(R.array.pic_sources)[picSource!!.nameIndex],
						onValueChange = {},
						readOnly = true,
						trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
						modifier = Modifier
							.menuAnchor(MenuAnchorType.PrimaryNotEditable)
					)
					ExposedDropdownMenu(
						expanded = expanded,
						onDismissRequest = { expanded = false },
					) {
						stringArrayResource(R.array.pic_sources).withIndex().map { (index, name) ->
							DropdownMenuItem(
								text = {
									Text(
										text = name,
										fontSize = Typography.bodyLarge.fontSize * tsf,
										fontFamily = montserrat,
									)
								},
								onClick = {
									when (index) {
										0 -> {
											picSource = ProfilePicSources.NoChange(user.pic)
										}
										
										1 -> {
											urlPopupShowing = true
										}
										
										2 -> {
											pickingColour = true
										}
										
										3 -> {
											requestCameraPermission({}) {
												openImagePicker = true
											}
										}
										
										else -> {}
									}
								},
								contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
							)
						}
					}
				}
				Spacer(modifier = Modifier.height(8.dp))
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
						loading = true
						
						if (picSource!!.type == "url") {
							coroutineScope.launch {
								loadingBringIntoViewRequester.bringIntoView()
								val res = client!!.requestMultipartFromAPI(
									path = "update-profile",
									parts = formData {
										append(
											key = "info",
											value = Json.encodeToString(
												ProfileUpdateBody(
													email = email,
													pic = picSource!!.source,
													hasPicFile = false,
													status = status,
													displayName = displayName,
													uid = user.uid,
													password = password.ifEmpty { null },
												),
											),
											headers = Headers.build {
												append(HttpHeaders.ContentType, "application/json")
											},
										)
									}
								)
								
								when (res.status) {
									HttpStatusCode.InternalServerError -> {
										Toast.makeText(
											context,
											context.resources.getString(R.string.save_changes_again),
											Toast.LENGTH_SHORT,
										).show()
									}
									
									HttpStatusCode.OK -> {
										viewModel.setUser(res.body<UserResponse>().payload!!)
										picSource = ProfilePicSources.NoChange(res.body<UserResponse>().payload!!.pic)
										loading = false
										Toast
											.makeText(
												context,
												context.resources.getString(R.string.saved_changes_successfully),
												Toast.LENGTH_SHORT,
											)
											.show()
									}
									
									HttpStatusCode.NotAcceptable -> {
										when (res.body<EmptyResponse>().message) {
											"Display name cannot be empty" -> {
												displayNameError = context.resources.getString(R.string.display_name_empty)
											}
											
											"Display name is too long" -> {
												displayNameError =
													context.resources.getString(R.string.display_name_max_length)
											}
											
											"Password is too short" -> {
												passwordError = context.resources.getString(R.string.password_length)
											}
											
											"Status is too long" -> {
												statusError = context.resources.getString(R.string.status_length)
											}
											
											"Email is incorrectly formatted" -> {
												emailError =
													context.resources.getString(R.string.email_formatted_incorrectly)
											}
										}
									}
								}
								
								loading = false
							}
						} else {
							coroutineScope.launch {
								loadingBringIntoViewRequester.bringIntoView()
								val timeStamp: String =
									SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
								val tempFile = File.createTempFile("IMG_${timeStamp}_", ".jpg", context.cacheDir)
								imageBitmap!!.asAndroidBitmap()
									.compress(Bitmap.CompressFormat.JPEG, 100, tempFile.outputStream())
								
								val res = client!!.requestMultipartFromAPI(
									path = "update-profile",
									parts = formData {
										append(
											key = "info",
											value = Json.encodeToString(
												ProfileUpdateBody(
													email = if (email == user.email) null else email,
													hasPicFile = true,
													status = if (status == user.status) null else status,
													displayName = if (displayName == user.displayName) null else displayName,
													uid = user.uid,
													password = password.ifEmpty { null },
												),
											),
											headers = Headers.build {
												append(HttpHeaders.ContentType, "application/json")
											},
										)
										append(
											key = "file",
											value = tempFile.readBytes(),
											headers = Headers.build {
												append(HttpHeaders.ContentType, "image/jpeg")
												append(
													HttpHeaders.ContentDisposition,
													"form-data; name=\"file\"; filename=\"profile-pic.jpg\""
												)
											}
										)
									}
								)
								
								tempFile.delete()
								
								when (res.status) {
									HttpStatusCode.PayloadTooLarge -> {
										picSource = ProfilePicSources.NoChange(user.pic)
										Toast.makeText(
											context,
											context.resources.getString(R.string.pic_too_large),
											Toast.LENGTH_SHORT,
										).show()
									}
									
									HttpStatusCode.InternalServerError -> {
										Toast.makeText(
											context,
											context.resources.getString(R.string.save_changes_again),
											Toast.LENGTH_SHORT,
										).show()
									}
									
									HttpStatusCode.OK -> {
										viewModel.setUser(res.body<UserResponse>().payload!!)
										picSource = ProfilePicSources.NoChange(res.body<UserResponse>().payload!!.pic)
										loading = false
										Toast.makeText(
											context,
											context.resources.getString(R.string.saved_changes_successfully),
											Toast.LENGTH_SHORT,
										).show()
									}
									
									HttpStatusCode.NotAcceptable -> {
										when (res.body<EmptyResponse>().message) {
											"Display name cannot be empty" -> {
												displayNameError = context.resources.getString(R.string.display_name_empty)
											}
											
											"Display name is too long" -> {
												displayNameError =
													context.resources.getString(R.string.display_name_max_length)
											}
											
											"Password is too short" -> {
												passwordError = context.resources.getString(R.string.password_length)
											}
											
											"Status is too long" -> {
												statusError = context.resources.getString(R.string.status_length)
											}
											
											"Email is incorrectly formatted" -> {
												emailError =
													context.resources.getString(R.string.email_formatted_incorrectly)
											}
										}
									}
								}
								
								loading = false
							}
						}
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
	}
}