package ir.sharif.androidsample

import android.app.Application
import ir.sharif.androidsample.di.ServiceLocator

class AndroidSampleApp : Application() {
  override fun onCreate() {
    super.onCreate()
    ServiceLocator.init(this) // initialize tokens, repos, retrofit, etc.
  }
}
