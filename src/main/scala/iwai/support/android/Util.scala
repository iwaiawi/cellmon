package iwai.support.android

import java.util.{Calendar, Date}

import android.content.{BroadcastReceiver, Intent, Context}
import android.telephony.CellLocation
import android.view.View
import android.view.View.OnClickListener
import android.widget.TimePicker
import macroid.Ui

trait Implicits {

	import scala.language.implicitConversions

	implicit def func2BroadcastReceiver(f: (Context, Intent) => Unit): BroadcastReceiver = new BroadcastReceiver {
		def onReceive(context: Context, intent: Intent) = f(context, intent)
	}

	implicit def func2OnClickListener(f: View => Unit): OnClickListener = new OnClickListener {
		override def onClick(v: View): Unit = f(v)
	}

		implicit class ViewWrapper(view: View) {

			def onClick(f: View => Ui[_]): Unit = view.setOnClickListener(new OnClickListener {
				override def onClick(v: View): Unit = runUi(f(v))
			})
		}

	//	implicit class TimePickerWrapper(picker: TimePicker) {
	//		def getCurrentTime(): Long = getCurrentCalendar.getTimeInMillis
	//
	//		def getCurrentDate(): Date = getCurrentCalendar.getTime
	//
	//		def getCurrentCalendar(): Calendar = {
	//			val c = Calendar.getInstance
	//			c.set(Calendar.HOUR_OF_DAY, picker.getCurrentHour)
	//			c.set(Calendar.MINUTE, picker.getCurrentMinute)
	//			c.set(Calendar.SECOND, 0)
	//			c.set(Calendar.MILLISECOND, 0)
	//			c
	//		}
	//	}

	implicit class ComparableDate(val d: Date) {
		def <(another: Date): Boolean = d.getTime < another.getTime

		def >(another: Date): Boolean = d.getTime > another.getTime

		def <=(another: Date): Boolean = d.getTime <= another.getTime

		def >=(another: Date): Boolean = d.getTime >= another.getTime
	}

}

object Implicits extends Implicits