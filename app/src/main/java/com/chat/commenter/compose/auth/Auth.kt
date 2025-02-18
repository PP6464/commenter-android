package com.chat.commenter.compose.auth

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chat.commenter.LocalNavController
import com.chat.commenter.Page
import com.chat.commenter.R
import com.chat.commenter.api.EmptyResponse
import com.chat.commenter.api.UserResponse
import com.chat.commenter.api.requestFromAPI
import com.chat.commenter.state.AppViewModel
import com.chat.commenter.ui.theme.montserrat
import io.ktor.client.call.body
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Auth(
	viewModel: AppViewModel = koinViewModel(),
) {
	var tabIndex by remember { mutableIntStateOf(0) }
	val pagerState = rememberPagerState(
		initialPage = tabIndex,
		initialPageOffsetFraction = 0F,
		pageCount = { 2 }
	)
	val tabs = listOf(
		stringResource(id = R.string.login),
		stringResource(id = R.string.sign_up),
	)
	val coroutineScope = rememberCoroutineScope()
	val ui by viewModel.uiState.collectAsState()
	val tsf = ui.tsf
	val navController = LocalNavController.current
	val httpClient by viewModel.clientState.collectAsState()
	val context = LocalContext.current
	
	LaunchedEffect(pagerState) {
		snapshotFlow { pagerState.currentPage }.collect {
			tabIndex = it
		}
	}
	
	LaunchedEffect(Unit) {
		val res = httpClient!!.requestFromAPI("re-auth", HttpMethod.Get)
		if (res.status == HttpStatusCode.OK) {
			viewModel.setUser(res.body<UserResponse>().payload!!)
			navController.navigate(Page.Home.route) {
				popUpTo(Page.Auth.route) { inclusive = true }
			}
		} else if (res.status == HttpStatusCode.NotAcceptable && res.body<EmptyResponse>().message == "JWT is expired") {
			Toast
				.makeText(
					context,
					context.resources.getString(R.string.expired_session),
					Toast.LENGTH_SHORT,
				)
				.show()
		}
	}
	
	Scaffold(
		modifier = Modifier
			.fillMaxSize(),
		topBar = {
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
				colors = TopAppBarDefaults.topAppBarColors(
					containerColor = MaterialTheme.colorScheme.primary,
				)
			)
		},
	) { padding ->
		Column(
			modifier = Modifier
				.padding(padding)
				.fillMaxSize()
		) {
			PrimaryTabRow(
				selectedTabIndex = tabIndex,
				contentColor = MaterialTheme.colorScheme.onPrimary,
				containerColor = MaterialTheme.colorScheme.primary.copy(
					red = MaterialTheme.colorScheme.primary.red * 0.9F,
					green = MaterialTheme.colorScheme.primary.green * 0.9F,
					blue = MaterialTheme.colorScheme.primary.blue * 0.9F,
				),
				indicator = {
					Box(
						contentAlignment = when (tabIndex) {
							0 -> Alignment.BottomStart
							else -> Alignment.BottomEnd
						},
						modifier = Modifier
							.fillMaxWidth()
							.height(3.dp)
							.tabIndicatorOffset(tabIndex)
					) {
						Box(
							modifier = Modifier
								.fillMaxWidth(1F)
								.height(3.dp)
								.background(MaterialTheme.colorScheme.secondary)
						)
					}
				}
			) {
				tabs.forEachIndexed { index, title ->
					Tab(
						text = {
							Text(
								text = title,
								style = TextStyle(
									fontFamily = montserrat,
									fontWeight = if (tabIndex == index) FontWeight.Bold else FontWeight.Normal,
									fontSize = (if (tabIndex == index) 22.sp else 20.sp) * tsf,
								)
							)
						},
						selected = index == tabIndex,
						onClick = {
							tabIndex = index
							coroutineScope.launch {
								pagerState.animateScrollToPage(index)
							}
						}
					)
				}
			}
			HorizontalPager(state = pagerState) {
				when (it) {
					0 -> Login {
						tabIndex = 1
						coroutineScope.launch {
							pagerState.animateScrollToPage(1)
						}
					}
					else -> SignUp {
						tabIndex = 0
						coroutineScope.launch {
							pagerState.animateScrollToPage(0)
						}
					}
				}
			}
		}
	}
}