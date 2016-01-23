package iwai.cellmon.model.core.entity.cell

import java.util.Date

import iwai.cellmon.model.core.entity.{LocationProvider, Location}
import spray.json.{JsonFormat, DefaultJsonProtocol}
import Location.JsonProtocol._
import LocationProvider.JsonProtocol._


case class CellLocationLog[+A <: Cell](cell: A, location: Location, timeInMillis: Long, provider: LocationProvider) {
	def time: Date = new Date(timeInMillis)
}

case object CellLocationLog {
	def apply[A <: Cell](cell: A, l: android.location.Location, provider: LocationProvider): CellLocationLog[A] =
		CellLocationLog(cell, Location(l), l.getTime, provider)

//	trait JsonProtocol extends DefaultJsonProtocol {
//
//		implicit def cellLocationLogFormat[A <: Cell : JsonFormat]: JsonFormat[CellLocationLog[A]] =
//			jsonFormat(CellLocationLog.apply[A], "cell", "location", "timeInMillis", "provider")
//	}
//
//	object JsonProtocol extends JsonProtocol

}
