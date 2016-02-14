package iwai.cellmon.model.core.repository.cellchange

import iwai.cellmon.model.core.entity.cell.CellChange
import iwai.cellmon.model.core.entity.common.Period
import iwai.cellmon.model.core.repository.{MultiOpRepository, Repository}

trait CellChangeRepository extends Repository[CellChange => Boolean, CellChange]
with MultiOpRepository[Period, CellChange => Boolean, CellChange] {

}