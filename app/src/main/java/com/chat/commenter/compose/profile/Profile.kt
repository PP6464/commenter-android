package com.chat.commenter.compose.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import coil3.compose.AsyncImage
import com.chat.commenter.Page
import com.chat.commenter.compose.elements.AppBar
import com.chat.commenter.state.AppViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun Profile(
	viewModel: AppViewModel = koinViewModel(),
) {
	val userState by viewModel.userState.collectAsState()
	val user = userState.user!!
	
	Scaffold(
		topBar = {
			AppBar(
				pageToHide = Page.Profile,
			)
		}
	) { padding ->
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(padding)
		) {
			AsyncImage(
				model = user.pic,
				contentDescription = null,
			)
		}
	}
}