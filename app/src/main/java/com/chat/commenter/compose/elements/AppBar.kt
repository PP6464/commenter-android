package com.chat.commenter.compose.elements

import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.chat.commenter.LocalNavController
import com.chat.commenter.Page
import com.chat.commenter.R
import com.chat.commenter.api.apiUrl
import com.chat.commenter.api.requestFromAPI
import com.chat.commenter.state.AppViewModel
import com.chat.commenter.ui.theme.Typography
import com.chat.commenter.ui.theme.montserrat
import io.ktor.client.plugins.cookies.cookies
import io.ktor.http.HttpMethod
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
	currentPage: Page? = null,
	viewModel: AppViewModel = koinViewModel(),
) {
	val ui by viewModel.uiState.collectAsState()
	val tsf = ui.tsf
	val httpClient by viewModel.clientState.collectAsState()
	var expanded by remember { mutableStateOf(false) }
	val navController = LocalNavController.current
	val coroutineScope = rememberCoroutineScope()
	
	TopAppBar(
		title = {
			SelectionContainer {
				Text(
					text = stringResource(id = R.string.app_name),
					fontFamily = montserrat,
					fontWeight = FontWeight.Bold,
					fontSize = 25.sp * tsf,
				)
			}
		},
		actions = {
			IconButton(onClick = { expanded = !expanded }) {
				Icon(
					imageVector = Icons.Filled.Menu,
					contentDescription = null,
				)
			}
			DropdownMenu(
				expanded = expanded,
				onDismissRequest = { expanded = !expanded }
			) {
				currentPage?.let {
					DropdownMenuItem(
						text = {
							Text(
								stringResource(it.nameId),
								fontSize = 18.sp * tsf,
								fontWeight = FontWeight.Bold,
								fontFamily = montserrat,
							)
						},
						onClick = {
							expanded = false
						}
					)
				}
				Page.entries.filter { it !in setOf(Page.Auth, currentPage) }.map {
					DropdownMenuItem(
						text = {
							Text(
								stringResource(it.nameId),
								fontSize = Typography.bodyLarge.fontSize * tsf,
								fontFamily = montserrat,
							)
						},
						onClick = {
							navController.navigate(it.route)
						},
					)
				}
				DropdownMenuItem(
					text = {
						Text(
							stringResource(id = R.string.logout),
							fontSize = Typography.bodyLarge.fontSize * tsf,
							fontFamily = montserrat,
							color = MaterialTheme.colorScheme.error,
						)
					},
					onClick = {
						coroutineScope.launch {
							httpClient!!.requestFromAPI("logout", HttpMethod.Post)
							println("CHECK COOKIES ARE EMPTY")
							println(httpClient!!.cookies(apiUrl))
							navController.navigate(Page.Auth.route) {
								popUpTo(navController.currentDestination!!.route!!) { inclusive = true }
							}
							viewModel.logout()
						}
					},
				)
			}
		},
		colors = TopAppBarDefaults.topAppBarColors(
			containerColor = MaterialTheme.colorScheme.primary,
		)
	)
}