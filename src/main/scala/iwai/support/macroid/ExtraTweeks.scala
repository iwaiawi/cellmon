package iwai.support.macroid

import java.util.Date

import android.text.format.DateFormat
import android.widget.TextView
import macroid.{Tweak, ContextWrapper}

object DateTimeTextViewTweaks {
	type W = TextView

	def tvDate(date: Date)(implicit context: ContextWrapper): Tweak[W] =
		Tweak[W](_.setText(DateFormat.format("yyyy/MM/dd HH:mm:ss", date)))

}
