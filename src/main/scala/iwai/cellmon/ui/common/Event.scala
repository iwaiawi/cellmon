package iwai.cellmon.ui.common

import iwai.cellmon.model.core.entity.cell.CellChange

import scala.util.Try

trait Event[A] {
	def result: Try[A]
	def get = result
}



case class CellChangePutCompleteEvent(val result: Try[CellChange]) extends Event[CellChange]
