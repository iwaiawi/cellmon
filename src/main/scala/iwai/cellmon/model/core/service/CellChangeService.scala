package iwai.cellmon.model.core.service

import iwai.cellmon.model.core.entity.cell.CellChange
import iwai.cellmon.model.core.entity.common.Period
import iwai.cellmon.model.core.repository.cellchange._
import iwai.cellmon.model.core.entity.common.Day

import scalaz.concurrent.Task

class CellChangeService(repo: CellChangeRepository) {
	def store(change: CellChange): Task[(CellChangeService, CellChange)] = {
		repo.put(change).map(t => (this, t._2))
	}

	def search(period: Period): Task[Seq[CellChange]] = {
		repo.getMulti(period)
	}

	def delete(period: Period): Task[(CellChangeService, Seq[CellChange])] = {
		repo.removeMulti(period).map(t => (this, t._2))
	}
}

object CellChangeService {
	def apply(repo: CellChangeRepository): CellChangeService = new CellChangeService(repo)
}
