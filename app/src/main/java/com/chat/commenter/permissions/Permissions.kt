package com.chat.commenter.permissions

import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
fun rememberPermissionRequester(
	permission : String,
) : (onDenied: () -> Unit, onGranted: () -> Unit) -> Unit {
	val context = LocalContext.current
	var permissionState by remember { mutableStateOf(ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) }
	var pendingOnGranted by remember { mutableStateOf<(() -> Unit)?>(null) }
	var pendingOnDenied by remember { mutableStateOf<(() -> Unit)?>(null) }
	
	val permissionRequest = rememberLauncherForActivityResult(
		contract = ActivityResultContracts.RequestPermission(),
	) {
		permissionState = it
		
		if (it) {
			pendingOnGranted?.invoke()
			pendingOnGranted = null
		} else {
			pendingOnDenied?.invoke()
			pendingOnDenied = null
		}
	}
	
	return { onDenied, onGranted ->
		pendingOnGranted = onGranted
		pendingOnDenied = onDenied
		
		if (permissionState) {
			onGranted()
		} else {
			permissionRequest.launch(permission)
		}
	}
}