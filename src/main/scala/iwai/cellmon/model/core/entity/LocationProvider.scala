package iwai.cellmon.model.core.entity

import spray.json._

sealed abstract class LocationProvider(val name: String)

case object ManualLocationProvider extends LocationProvider("MANUAL")

case object GpsLocationProvider extends LocationProvider("GPS")

case object LocationProvider {

	trait JsonProtocol extends DefaultJsonProtocol {

		implicit object LocationProviderFormat extends RootJsonFormat[LocationProvider] {
			override def write(p: LocationProvider): JsValue = p.name.toJson

			override def read(value: JsValue): LocationProvider = value match {
				case JsString(name) if name == ManualLocationProvider.name => ManualLocationProvider
				case JsString(name) if name == GpsLocationProvider.name => GpsLocationProvider
				case _ => deserializationError("Expected LocationProvider as JsString, but got " + value)
			}
		}

	}

	object JsonProtocol extends JsonProtocol

}