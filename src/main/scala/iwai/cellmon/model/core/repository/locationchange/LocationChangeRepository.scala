package iwai.cellmon.model.core.repository.locationchange

import iwai.cellmon.model.core.entity.common.Period
import iwai.cellmon.model.core.entity.location.LocationChange
import iwai.cellmon.model.core.repository.{MultiOpRepository, Repository}

trait LocationChangeRepository extends Repository[LocationChange => Boolean, LocationChange]
	with MultiOpRepository[Period, LocationChange => Boolean, LocationChange] {

}