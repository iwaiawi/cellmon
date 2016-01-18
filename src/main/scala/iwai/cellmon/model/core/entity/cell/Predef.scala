package iwai.cellmon.model.core.entity.cell

trait JsonProtocol
	extends Cell.JsonProtocol
	with CellChange.JsonProtocol
//	with CellGroup.JsonProtocol
	with CellLocationLog.JsonProtocol

object JsonProtocol extends JsonProtocol