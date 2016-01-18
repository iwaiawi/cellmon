package iwai.cellmon.model.core.repository

import java.util.Date

import iwai.cellmon.model.core.entity.cell.CellChange
import iwai.cellmon.model.core.entity.common.Period
import iwai.cellmon.model.core.repository.cellchange.CellChangeRepository

package object cellchange {
	type ID = ENTITY => Boolean
	type ENTITY = CellChange
//	type REPO = CellChangeRepository
	type QUERY = Period

}
