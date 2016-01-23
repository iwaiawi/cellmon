package iwai.cellmon.model.core.entity.cell

import java.util.Date
import spray.json.{JsValue, JsonFormat, RootJsonFormat, DefaultJsonProtocol}

case class CellChange(changeAtInMillis: Long, cell: Cell) {
	def changeAt: Date = new Date(changeAtInMillis)
}

case object CellChange {
	def apply(cell: Cell): CellChange =
		CellChange(System.currentTimeMillis(), cell)

//	trait JsonProtocol extends DefaultJsonProtocol {
//
//		implicit val cellChangeFormat = jsonFormat(CellChange.apply, "changeAtInMillis", "cell")
//	}
//
//	object JsonProtocol extends JsonProtocol

}
