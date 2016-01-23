package iwai.cellmon.ui.service

import java.util

import android.app.{IntentService, Service}
import android.content.{Context, Intent}
import android.os.IBinder
import android.telephony._
import android.util.Log
import iwai.cellmon.model.support.android.repository.cellchange.CellChangeFileRepository
import iwai.cellmon.ui._
import iwai.cellmon.model.core.entity.cell.{Cell, CellChange}
//import iwai.cellmon.model.core.entity.cell.JsonProtocol._
import iwai.cellmon.model.core.service.CellChangeService
import iwai.cellmon.ui.common.CellChangePutCompleteEvent
import macroid.{Contexts, ContextWrapper}
import macroid.FullDsl._
import spray.json._
import timber.log.Timber

import scala.util.Try
import scalaz.{\/-, -\/}
import scalaz.concurrent.Task

class CellChangeListener extends Service
with Contexts[Service] {
	self =>

	override def onBind(intent: Intent): IBinder = {
		null // should not bind
	}

	override def onCreate(): Unit = {
		super.onCreate()
		registerPhoneStateListener(
			listener,
			PhoneStateListener.LISTEN_CELL_LOCATION | PhoneStateListener.LISTEN_CELL_INFO
		)
	}

	override def onStartCommand(intent: Intent, flags: Int, startId: Int): Int = {
		onStart(intent, startId)
		Service.START_STICKY
	}

	lazy val listener = new PhoneStateListener {
		override def onCellLocationChanged(l: android.telephony.CellLocation) = {
			val change = CellChange(Cell(l))
			storeCellChange(change)
		}

		override def onCellInfoChanged(info: java.util.List[CellInfo]) = {
			Option(info).foreach { i =>
				import collection.JavaConversions._
				val change = CellChange(Cell(i.toList))
				storeCellChange(change)
			}
		}
	}

	def storeCellChange(change: CellChange): Unit = {
		val repo = new CellChangeFileRepository
		val task: Task[(CellChangeService, CellChange)] = CellChangeService(repo).store(change)
		//			task.map { (_, c) =>
		//				CellChangeService(S3Repository2).store(c)
		//			}

		task.runAsync {
			case \/-((_, result)) => eventBus.post(CellChangePutCompleteEvent(Try(result)))
			case -\/(e) => Timber.e(e, e.getMessage)
		}
	}

	override def onStart(intent: Intent, startId: Int): Unit = {
	}

	def registerPhoneStateListener(listener: PhoneStateListener, events: Int) {
		getSystemService(Context.TELEPHONY_SERVICE)
				.asInstanceOf[TelephonyManager]
				.listen(listener, events)
	}

	def unregisterPhoneStateListener(listener: PhoneStateListener) {
		getSystemService(Context.TELEPHONY_SERVICE)
				.asInstanceOf[TelephonyManager]
				.listen(listener, PhoneStateListener.LISTEN_NONE)
	}

	override def onDestroy(): Unit = {
		super.onDestroy()
	}

	override def onLowMemory(): Unit = {
		super.onLowMemory()
	}

	override def onTrimMemory(level: Int): Unit = {
		super.onTrimMemory(level)
	}
}
