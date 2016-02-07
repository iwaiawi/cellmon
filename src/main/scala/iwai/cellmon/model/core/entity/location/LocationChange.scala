package iwai.cellmon.model.core.entity.location

import java.util.Date

import iwai.cellmon.model.core.entity.cell.Cell


case class LocationChange(changeAtInMillis: Long, location: Location, cell: Option[Cell]) {
  def changeAt: Date = new Date(changeAtInMillis)
}

case object LocationChange {
  def apply(location: Location, cell: Option[Cell]): LocationChange =
    LocationChange(System.currentTimeMillis(), location, cell)

}
