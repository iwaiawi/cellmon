package iwai.cellmon.model.core.service

import iwai.cellmon.model.core.entity.common.Period
import iwai.cellmon.model.core.entity.location.LocationChange
import iwai.cellmon.model.core.repository.locationchange.LocationChangeRepository

import scalaz.concurrent.Task

class LocationChangeService(repo: LocationChangeRepository) {
	def store(change: LocationChange): Task[(LocationChangeService, LocationChange)] = {
		repo.put(change).map(t => (this, t._2))
	}

	def search(period: Period): Task[Seq[LocationChange]] = {
		repo.getMulti(period)
	}

	def delete(period: Period): Task[(LocationChangeService, Seq[LocationChange])] = {
		repo.removeMulti(period).map(t => (this, t._2))
	}
}

object LocationChangeService {
	def apply(repo: LocationChangeRepository): LocationChangeService = new LocationChangeService(repo)
}

