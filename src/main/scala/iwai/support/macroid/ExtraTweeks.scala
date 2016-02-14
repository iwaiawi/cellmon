package iwai.support.macroid

import java.util.Date

import android.text.format.DateFormat
import android.view.View
import android.widget.{Checkable, TextView}
import com.fortysevendeg.macroid.extras.TextTweaks._
import iwai.cellmon.model.core.entity.cell._
import iwai.cellmon.model.core.entity.location.Location
import macroid.{Tweak, ContextWrapper}

object CheckableViewTweaks {
	type W = View with Checkable

	def cvToggle()(implicit context: ContextWrapper): Tweak[W] =
		Tweak[W](_.toggle())
	def cvChecked(check: Boolean)(implicit context: ContextWrapper): Tweak[W] =
		Tweak[W](_.setChecked(check))
}

object DateTimeTextViewTweaks {
	type W = TextView

	def tvTimestamp(date: Date)(implicit context: ContextWrapper): Tweak[W] =
		Tweak[W](_.setText(DateFormat.format("yyyy/MM/dd HH:mm:ss", date)))

	def tvCell(cell: Cell): Tweak[TextView] = cell match {
		case GsmCellLocation(lac: Int, cid: Int, psc: Int) =>
			tvText("GSM(lac:%05d, cid:%09d, psc:%03d)".format(lac, cid, psc))
		case LteCellIdentity(mcc: Int, mnc: Int, ci: Int, pci: Int, tac: Int) =>
			tvText("LTE(mcc:%03d, mnc:%02d, ci:%09d, pci:%03d, tac:%05d)".format(mcc, mnc, ci, pci, tac))
		case WcdmaCellIdentity(mcc: Int, mnc: Int, lac: Int, cid: Int, psc: Int) =>
			tvText("WCDMA(mcc:%03d, mnc:%02d, lac:%05d, ci:%09d, psc:%03d)".format(mcc, mnc, lac, cid, psc))
		case _: NoCell => tvText("No Cell")
		case _ => tvText(cell.toString)
	}

	def tvLocation(loc: Location): Tweak[TextView] =
		tvText("Location(lat:%.6f, lon:%.6f, acc:%.1f)".format(loc.latitude, loc.longitude, loc.accuracy))

}
