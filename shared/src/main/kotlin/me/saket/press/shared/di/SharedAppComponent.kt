package me.saket.press.shared.di

import android.app.Application
import androidx.preference.PreferenceManager
import com.russhwolf.settings.AndroidSettings
import com.squareup.sqldelight.android.AndroidSqliteDriver
import me.saket.press.PressDatabase

actual object SharedAppComponent : BaseSharedAppComponent() {

  fun initialize(appContext: Application) {
    setupGraph(PlatformDependencies(
        sqlDriver = { androidSqliteDriver(appContext) },
        settings = { androidSettings(appContext) }
    ))
  }

  private fun androidSqliteDriver(appContext: Application) =
    AndroidSqliteDriver(PressDatabase.Schema, appContext, "press.db")

  private fun androidSettings(appContext: Application) =
    AndroidSettings(PreferenceManager.getDefaultSharedPreferences(appContext))
}
