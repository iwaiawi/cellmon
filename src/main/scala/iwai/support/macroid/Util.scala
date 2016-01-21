package iwai.support.macroid

import android.app.{Service, Activity}
import android.content.{BroadcastReceiver, Intent, Context, DialogInterface}
import android.content.DialogInterface.OnClickListener
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.telephony.{PhoneStateListener, CellLocation}
import android.view.{WindowManager, View, Gravity}
import android.widget.{TableLayout, TableRow}

import macroid._
import macroid.FullDsl._

import scala.concurrent.Future
import scala.tools.nsc.backend.icode.Opcodes.opcodes.CALL_METHOD
import scala.tools.nsc.backend.icode.Opcodes.opcodes.CALL_METHOD
import scala.util.Try
import scalaz.\/
import scalaz.concurrent.Task

trait Implicits {

	import scala.language.implicitConversions

	implicit def func02OnClickListener(f: () => Ui[Any]) = new OnClickListener {
		def onClick(dialog: DialogInterface, which: Int): Unit = f().get
	}

	implicit def func2PhoneStateListener(f: (CellLocation) => Ui[Any]) = new PhoneStateListener {
		override def onCellLocationChanged(c: CellLocation) = f(c).run
	}

	/** Helpers to run UI actions as Task callbacks */
	implicit class UiTask[T](task: Task[T]) {

		def runAsyncUi[S](f: (Throwable \/ T) => Ui[S]): Unit =
			task.runAsync(f.andThen(_.run))
	}
}

object Implicits extends Implicits

//trait Contexts[X] extends macroid.Contexts[X] { self: X ⇒
//	implicit def serviceAppContext(implicit service: X <:< Service) =
//		AppContext(service(self).getApplicationContext)
//}

trait Activities {

	def keepScreenOn(f: Boolean)(implicit activity: ActivityContextWrapper) = {
		val window = activity.getOriginal.getWindow
		if(f) {
			window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
		} else {
			window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
		}
	}
}

object Activities extends Activities

trait Tweaks {

	import scala.language.reflectiveCalls
	type Gravitiable = View {def setGravity(g: Int)}

	def gravity[W <: Gravitiable](g: Int) = Tweak[W](_.setGravity(g))

	val center = gravity(Gravity.CENTER)

	def focus(f: Boolean) = Tweak[View](_.setFocusable(f))

	def border[W <: View](width: Int = 1, color: Int = Color.BLACK, backgroundColor: Int = Color.WHITE) = {
		Tweak[W] { v =>
			val d = new GradientDrawable()
			d.setStroke(width, color)
			d.setColor(backgroundColor)
			v.setBackground(d)
		}
	}

	def stretchAllColumns(f: Boolean) = Tweak[TableLayout](_.setStretchAllColumns(f))

	def span(span: Int) = {
		Tweak[View] { v =>
			val params = Option(v.getLayoutParams) match {
				case None => new TableRow.LayoutParams
				case Some(p) => new TableRow.LayoutParams(p)
			}
			params.span = span
			v.setLayoutParams(params)
		}
	}
}

object Tweaks extends Tweaks
