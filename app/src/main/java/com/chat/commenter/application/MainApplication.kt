package com.chat.commenter.application

import android.app.Application
import com.chat.commenter.state.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MainApplication : Application() {
	override fun onCreate() {
		super.onCreate()
		
		startKoin {
			androidLogger()
			androidContext(this@MainApplication)
			modules(appModule)
		}
	}
}