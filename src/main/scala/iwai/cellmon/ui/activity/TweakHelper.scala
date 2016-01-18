package iwai.cellmon.ui.activity

import java.text.SimpleDateFormat
import java.util.{Date, TimeZone}

import android.content.Context
import android.graphics.Color
import android.telephony.TelephonyManager
import android.view.ViewGroup.LayoutParams._
import android.view.{View, ViewGroup}
import android.widget.{EditText, LinearLayout, TextView}
import iwai.cellmon.model.core.entity.cell.Cell
//import iwai.cellmon.util.TickTime
import iwai.support.macroid.Tweaks._
import macroid.FullDsl._
import macroid.contrib.TextTweaks._
import macroid.{ContextWrapper, Tweak}

/**
	* Created by iwai on 2016/01/15.
	*/
trait TweakHelper {
	def menuButtonStyle(implicit ctx: ContextWrapper) = {
		large +
				lp[ViewGroup](MATCH_PARENT, WRAP_CONTENT) +
				Tweak[View] { v =>
					import scala.language.postfixOps
					val params = new LinearLayout.LayoutParams(v.getLayoutParams)
					params.setMargins(20 dp, 20 dp, 20 dp, 20 dp)
					v.setLayoutParams(params)
				}
	}

	val ROYAL_BLUE: Int = Color.parseColor("#4169E1")

	val headerCell = center +
			color(Color.WHITE) +
			border(color = Color.DKGRAY, backgroundColor = ROYAL_BLUE)

	val dataCell = center +
			color(Color.BLACK) +
			border(color = Color.DKGRAY, backgroundColor = Color.WHITE)


//	def updateLog(logs: Iterable[SwitchingLog]): Tweak[EditText] = {
//		text(logs.toSeq.reverse.map(_.toString()).mkString("\n"))
//	}

	def updateLog(logs: Iterable[Cell]): Tweak[EditText] = {
		text(logs.toSeq.reverse.map(_.toString()).mkString("\n"))
	}

//	def updateResult(logs: Iterable[SwitchingLog]): Tweak[TextView] = {
//		text("Attach %d / Detach %d".format(logs.count(_.isAttach), logs.count(_.isDetach)))
//	}

//	def updateStartTime(time: Option[TickTime]): Tweak[TextView] = {
//		time.map { t =>
//			text("%tT (%s)".format(t.startAt, formatDiff(t.elapsed)))
//		}.getOrElse(text("--:--:-- (--:--.---)"))
//	}
//
//	def updateEndTime(time: Option[TickTime]): Tweak[TextView] = {
//		time.map { t =>
//			text("%tT (%s)".format(t.endAt, formatDiff(-t.remaining)))
//		}.getOrElse(text("--:--:-- (--:--.---)"))
//	}

	def formatDiff(diff: Long): String = {
		val op = if(diff >= 0) "+" else "-"
		val sdf = new SimpleDateFormat("HH:mm:ss.SSS")
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"))
		"%s%s".format(op, sdf.format(new Date(diff.abs)))
	}

//	def updateStartTime(time: TickTime): Tweak[TextView] = updateStartTime(Option(time))
//
//	def updateEndTime(time: TickTime): Tweak[TextView] = updateEndTime(Option(time))

//	val updateStartTime: Tweak[TextView] = updateStartTime(None)
//
//	val updateEndTime: Tweak[TextView] = updateEndTime(None)

	def updateCellInfo(cell: Option[Cell]): Tweak[TextView] = {
		(cell.collect {
			case c: android.telephony.gsm.GsmCellLocation => (c.getLac, c.getCid)
			case c: android.telephony.cdma.CdmaCellLocation => (c.getSystemId, c.getBaseStationId)
		} filter {
			case (_, -1) => false
			case (-1, _) => false
			case _ => true
		} collect {
			case (lac: Int, cid: Int) => text("LAC:%d/CID:%d".format(lac, cid))
		}).getOrElse(text("LAC:--/CID:--"))
	}

	val updateCellInfo: Tweak[TextView] = updateCellInfo(None)

	def updateBearer(implicit ctx: ContextWrapper): Tweak[TextView] = {
		import android.telephony.TelephonyManager._

		(ctx.bestAvailable.getSystemService(Context.TELEPHONY_SERVICE)
				.asInstanceOf[TelephonyManager]
				.getNetworkType match {
			case NETWORK_TYPE_UMTS => Option("3G")
			case NETWORK_TYPE_HSDPA => Option("HSDPA")
			case NETWORK_TYPE_LTE => Option("LTE")
			case _ => None
		}).map(text)
				.getOrElse(text("unknown"))
	}
}
