package iwai.cellmon.model.core.entity.location

case class Location(latitude: Double, longitude: Double, accuracy: Float) {
	//	def geoHash: String
}

case object Location {
	def apply(l: android.location.Location): Location =
		apply(l.getLatitude, l.getLongitude, l.getAccuracy)
}