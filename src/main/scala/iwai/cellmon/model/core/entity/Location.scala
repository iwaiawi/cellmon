package iwai.cellmon.model.core.entity

import spray.json.DefaultJsonProtocol

case class Location(latitude: Double, longitude: Double, accuracy: Float) {
	//	def geoHash: String
}

case object Location {
	def apply(l: android.location.Location): Location =
		apply(l.getLatitude, l.getLongitude, l.getAccuracy)

	trait JsonProtocol extends DefaultJsonProtocol {
		implicit val locationFormat = jsonFormat(Location.apply, "latitude", "longitude", "accuracy")
	}

	object JsonProtocol extends JsonProtocol
}