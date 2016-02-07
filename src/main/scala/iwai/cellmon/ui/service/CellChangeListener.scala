package iwai.cellmon.ui.service

import android.app.Service
import android.content.Context
import android.telephony._
import iwai.cellmon.model.core.entity.cell.{NoCell, Cell, CellChange}
import iwai.cellmon.model.core.service.CellChangeService
import iwai.cellmon.model.support.android.repository.cellchange.CellChangeFileRepository
import iwai.cellmon.ui._
import iwai.cellmon.ui.common.CellChangePutCompleteEvent
import macroid.Contexts
import timber.log.Timber

import scala.util.Try
import scalaz.concurrent.Task
import scalaz.{-\/, \/-}

trait CellChangeListener {
  self: Service with Contexts[Service] =>

  def startCellChangeListener() = {
    registerPhoneStateListener(
      listener,
      PhoneStateListener.LISTEN_CELL_LOCATION | PhoneStateListener.LISTEN_CELL_INFO
    )
  }

  def stopCellChangeListener() = {
    registerPhoneStateListener(
      listener,
      PhoneStateListener.LISTEN_CELL_LOCATION | PhoneStateListener.LISTEN_CELL_INFO
    )
  }

  lazy private val listener = new PhoneStateListener {
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

  var lastCell: Option[Cell] = None

  def storeCellChange(change: CellChange): Unit = {
    lastCell = change.cell match {
      case _: NoCell => None
      case cell => Option(cell)
    }

    val repo = new CellChangeFileRepository
    val task: Task[(CellChangeService, CellChange)] = CellChangeService(repo).store(change)

    task.runAsync {
      case \/-((_, result)) => eventBus.post(CellChangePutCompleteEvent(Try(result)))
      case -\/(e) => Timber.e(e, e.getMessage)
    }
  }

  private def registerPhoneStateListener(listener: PhoneStateListener, events: Int) {
    getSystemService(Context.TELEPHONY_SERVICE)
      .asInstanceOf[TelephonyManager]
      .listen(listener, events)
  }

  private def unregisterPhoneStateListener(listener: PhoneStateListener) {
    getSystemService(Context.TELEPHONY_SERVICE)
      .asInstanceOf[TelephonyManager]
      .listen(listener, PhoneStateListener.LISTEN_NONE)
  }
}
