package iwai.cellmon.model.core.repository.cellchange

import iwai.cellmon.model.core.repository.{MultiOpRepository, Repository}

trait CellChangeRepository extends Repository[ID, ENTITY/*, CellChangeRepository*/]
with MultiOpRepository[QUERY, ID, ENTITY/*, CellChangeRepository*/] {

}