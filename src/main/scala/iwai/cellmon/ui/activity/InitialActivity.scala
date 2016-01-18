package iwai.cellmon.ui.activity

import android.app.{Service, Activity}
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget._
import iwai.cellmon.ui.activity.main.MainActivity
import macroid._

// import macroid stuff
import macroid.FullDsl._
import macroid.contrib.TextTweaks._

class InitialActivity extends Activity with TweakHelper with Contexts[Activity] {

	override def onCreate(savedInstanceState: Bundle) = {
		super.onCreate(savedInstanceState)

		val portraitLayout = l[LinearLayout](
			w[TextView] <~
					text("Airplane-mode Switcher") <~
					large,

			w[Button] <~
					text("Main") <~
					menuButtonStyle <~
					On.click {
						startActivity(classOf[MainActivity])
					},

			w[Button] <~
					text("設定") <~
					menuButtonStyle <~
					On.click {
						startActivity(classOf[SettingActivity])
					},

			w[Button] <~
					text("CellHistory") <~
					menuButtonStyle <~
					On.click {
						startActivity(classOf[CellHistoryActivity])
					}
		) <~
				vertical <~
				Tweak[LinearLayout](_.setGravity(Gravity.CENTER))

		val landscapeLayout = portraitLayout

		val layout = portrait ? portraitLayout | landscapeLayout
		setContentView(getUi(layout))
	}

	override def onDestroy() {
		super.onDestroy()
	}

	def startActivity[A <: Activity](clazz: Class[A])(implicit ctx: ContextWrapper): Ui[Unit] = {
		val i = new Intent(ctx.bestAvailable, clazz)
		startActivityForResult(i, 0)
		Ui.nop
	}

	def startService[A <: Service](clazz: Class[A])(implicit ctx: ContextWrapper): Ui[Unit] = {
		val i = new Intent(ctx.bestAvailable, clazz)
		startService(i)
		Ui.nop
	}

}
