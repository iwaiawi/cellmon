package iwai.cellmon.model.core.entity.cell

import java.util.Date

import iwai.cellmon.model.core.entity.{Location, LocationProvider}


case class CellLocationLog[+A <: Cell](cell: A, location: Location, timeInMillis: Long, provider: LocationProvider) {
  def time: Date = new Date(timeInMillis)
}

case object CellLocationLog {
  def apply[A <: Cell](cell: A, l: android.location.Location, provider: LocationProvider): CellLocationLog[A] =
    CellLocationLog(cell, Location(l), l.getTime, provider)

}
