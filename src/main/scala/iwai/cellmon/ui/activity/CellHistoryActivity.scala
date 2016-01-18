package iwai.cellmon.ui.activity

import java.io.{BufferedReader, InputStreamReader}
import java.util.Date
import java.util.concurrent.{CancellationException, ConcurrentLinkedQueue}

import android.content.{BroadcastReceiver, Context, Intent, IntentFilter}
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.ActionBarActivity
import android.telephony.{PhoneStateListener, TelephonyManager}
import android.view.ViewGroup.LayoutParams._
import android.view.{Gravity, ViewGroup}
import android.widget._
import de.greenrobot.event.EventBus
import iwai.cellmon.model._
import iwai.cellmon.model.core.entity.common.{Day}
import iwai.cellmon.model.support.android.repository.cellchange.CellChangeFileRepository
import iwai.cellmon.ui._
import iwai.cellmon.model.core.entity.cell.CellChange
import iwai.cellmon.model.core.service._
import iwai.cellmon.ui.common.CellChangePutCompleteEvent
import timber.log.Timber

import scalaz.{-\/, \/-}
import scalaz.concurrent.Task

//import iwai.cellmon.models.repository.TransparentIoContext
//import iwai.cellmon.services.CellChangeBroadcaster
//import iwai.cellmon.ui.activity.TweakHelper
import iwai.cellmon.ui.service.CellChangeListener
//import iwai.cellmon.utils.{CountDownTimerWrapper, Day, TickTime}
//import iwai.cellmon.views.TweakHelper
import macroid.{Contexts, Ui}

import scala.collection.JavaConversions._
import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success, Try}

// import macroid stuff

import iwai.support.android.Implicits._
import iwai.support.macroid.Activities._
import iwai.support.macroid.Implicits._
import iwai.support.macroid.Tweaks._
import macroid.FullDsl.{gravity => _, _}
import macroid.contrib.LpTweaks._
import macroid.contrib.TextTweaks._

class CellHistoryActivity
		extends ActionBarActivity
//		with TweakHelper
		with Contexts[ActionBarActivity] {

	private var startManualButton = slot[Button]

	private var startAutoButton = slot[Button]
	private var stopAutoButton = slot[Button]

	private var logView = slot[EditText]

	lazy val pref = PreferenceManager.getDefaultSharedPreferences(this)

	lazy val verticalLayout = l[LinearLayout](
		w[Button] <~
				wire(startManualButton) <~
				text("ボタン1") <~
				lp[ViewGroup](MATCH_PARENT, WRAP_CONTENT) <~
				On.click {
					logView <~ text(fileList().mkString("\n"))
				},
		w[Button] <~
				wire(startAutoButton) <~
				text("ボタン2") <~
				lp[ViewGroup](MATCH_PARENT, WRAP_CONTENT) <~
				enable <~
				On.click {
					val reader = new BufferedReader(
						new InputStreamReader(
							openFileInput(fileList().last),
							"UTF-8"
						)
					)
					val s = Iterator.continually(reader.readLine()).takeWhile(_ != null).toList
					reader.close()
					logView <~ text(s.mkString("\n"))
				},
		w[Button] <~
				wire(stopAutoButton) <~
				text("ボタン3") <~
				lp[ViewGroup](MATCH_PARENT, WRAP_CONTENT) <~
				disable <~
				On.click {
					Ui.nop
				},
		w[EditText] <~
				wire(logView) <~
				size(12) <~
				matchParent <~
				gravity(Gravity.TOP) <~
				focus(false)
	) <~
			matchParent <~
			vertical <~
			center

	override def onCreate(savedInstanceState: Bundle) = {
		super.onCreate(savedInstanceState)
		setContentView(getUi(verticalLayout))

		startService(new Intent(this, classOf[CellChangeListener]))
	}

	override def onResume = {
		super.onResume

		eventBus.register(this)
		refreshCellChange()
	}

	override def onPause = {
		eventBus.unregister(this)

		super.onPause
	}


	def refreshCellChange(): Unit = {
		//		CellChangeModel.find(Day.today())
		val repo = new CellChangeFileRepository
		val task: Task[Seq[CellChange]] = CellChangeService(repo).search(Day.today())
		task.map { result =>
			//			eventBus.post(CellChangeFindCompleteEvent(Try(result)))
			result
		}.runAsync {
			case \/-(changes) => runUi {
				logView <~ text(changes.mkString("\n"))
			}
			case -\/(e) => runUi {
				Timber.e(e, e.getMessage)
				toast(e.getMessage) <~ fry
			}
		}
	}

	def onEvent(ev: CellChangePutCompleteEvent): Unit =  {
		ev.get match {
			case Success(_) => refreshCellChange
			case Failure(e) => runUi(toast(e.getMessage) <~ fry)
		}
	}


//	def onEvent(ev: CellChangePutCompleteEvent): Unit = runUi {
//		ev.get match {
//			case Success(_) => refreshCellChange
//			case Failure(e) => toast(e.getMessage) <~ fry
//		}
//	}
//
//	def onEvent(ev: CellChangeDeleteCompleteEvent): Unit = runUi {
//		ev.get match {
//			case Success(_) => refreshCellChange
//			case Failure(e) => toast(e.getMessage) <~ fry
//		}
//	}

	override def onDestroy() = {
		super.onDestroy()
	}

	override def onBackPressed() = {
		super.onBackPressed()
	}
}

