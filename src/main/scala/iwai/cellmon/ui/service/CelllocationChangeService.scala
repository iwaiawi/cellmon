package iwai.cellmon.ui.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import macroid.Contexts

class CellLocationChangeListener extends Service
  with CellChangeListener
  with LocationChangeListener
  with Contexts[Service] {
  self =>

  override def onBind(intent: Intent): IBinder = {
    null // should not bind
  }

  override def onCreate(): Unit = {
    super.onCreate()
    startCellChangeListener()
    startLocationChangeListener()
  }

  override def onStartCommand(intent: Intent, flags: Int, startId: Int): Int = {
    onStart(intent, startId)
    Service.START_STICKY
  }

  override def onStart(intent: Intent, startId: Int): Unit = {
  }

  override def onDestroy(): Unit = {
    stopCellChangeListener()
    stopLocationChangeListener()
    super.onDestroy()
  }

  override def onLowMemory(): Unit = {
    super.onLowMemory()
  }

  override def onTrimMemory(level: Int): Unit = {
    super.onTrimMemory(level)
  }
}
