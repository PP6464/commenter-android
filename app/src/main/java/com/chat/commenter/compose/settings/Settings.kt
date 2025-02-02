package com.chat.commenter.compose.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.chat.commenter.Page
import com.chat.commenter.R
import com.chat.commenter.compose.elements.AppBar
import com.chat.commenter.state.AppViewModel
import com.chat.commenter.ui.theme.Typography
import com.chat.commenter.ui.theme.montserrat
import org.koin.androidx.compose.koinViewModel

@Composable
fun Settings(
	viewModel: AppViewModel = koinViewModel()
) {
	val uiModeOptions = listOf("system", "light", "dark")
	val localisedUiModeOptions = stringArrayResource(id = R.array.ui_mode_options)
	var uiMode by remember { mutableStateOf(viewModel.getUIMode()) }
	var tsf by remember { mutableFloatStateOf(viewModel.getTSF()) }
	
	Scaffold(
		topBar = {
			AppBar(
				pageToHide = Page.Settings,
			)
		},
		modifier = Modifier
			.fillMaxSize()
	) { padding ->
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(padding)
		) {
			Spacer(modifier = Modifier.height(8.dp))
			Text(
				stringResource(id = R.string.tsf),
				fontSize = Typography.bodyLarge.fontSize * tsf,
				fontFamily = montserrat,
				modifier = Modifier.padding(horizontal = 16.dp),
			)
			Spacer(modifier = Modifier.height(8.dp))
			Slider(
				value = tsf,
				onValueChange = {
					tsf = it
					viewModel.setTSF(it)
				},
				steps = 10,
				valueRange = 0.5F..1.5F,
				modifier = Modifier
					.padding(horizontal = 16.dp)
					.fillMaxWidth(),
			)
			Spacer(modifier = Modifier.height(8.dp))
			Text(
				stringResource(id = R.string.ui_mode),
				fontSize = Typography.bodyLarge.fontSize * tsf,
				fontFamily = montserrat,
				modifier = Modifier.padding(horizontal = 16.dp),
			)
			Spacer(modifier = Modifier.height(8.dp))
			uiModeOptions.withIndex().map { (index, text) ->
				Row(
					verticalAlignment = Alignment.CenterVertically,
					modifier = Modifier
						.padding(horizontal = 16.dp)
						.fillMaxWidth()
						.selectable(
							selected = text == uiMode,
							role = Role.RadioButton,
						) {
							uiMode = text
							viewModel.setUIMode(text)
						}
				) {
					RadioButton(
						selected = (text == uiMode),
						onClick = {
							uiMode = text
							viewModel.setUIMode(text)
						},
					)
					Text(
						localisedUiModeOptions[index],
						fontSize = Typography.bodyLarge.fontSize * tsf,
						fontFamily = montserrat,
					)
				}
			}
		}
	}
}