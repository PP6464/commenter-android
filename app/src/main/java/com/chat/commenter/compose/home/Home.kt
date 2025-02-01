package com.chat.commenter.compose.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.chat.commenter.Page
import com.chat.commenter.compose.elements.AppBar

@Composable
fun Home() {
	Scaffold(
		topBar = { AppBar(Page.Home) }
	) { padding ->
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(padding)
		) {
		
		}
	}
}