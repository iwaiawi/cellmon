package iwai.cellmon.model.support.android.repository.locationchange

import iwai.cellmon.model.core.entity.common.Period
import iwai.cellmon.model.core.entity.location.LocationChange
import iwai.cellmon.model.core.repository.locationchange._
import iwai.cellmon.model.support.android.repository._

import iwai.cellmon.model.core.entity.common._
import iwai.cellmon.model.core.entity.common.Period.Implicits._
import macroid.ContextWrapper
import spray.json._

import scalaz.concurrent.Task

import fommil.sjs.FamilyFormats._


class LocationChangeFileRepository(implicit val ctx: ContextWrapper)
	extends LocationChangeRepository
		with FileRepository[LocationChange => Boolean, LocationChange, Day]
		with MultiOpFileRepository[Period, LocationChange => Boolean, LocationChange, Day] {

	implicit val partitionFactory = new LocationChangeFilePartitionFactory

	override def entityToJson(entity: LocationChange): String = entity.toJson.compactPrint

	override def jsonToEntity(json: String): LocationChange = json.parseJson.convertTo[LocationChange]

	override def entityToPartitionKey(entity: LocationChange): Day = entity.changeAt.toDay

	override def isEntityThatMatchQuery(query: Period)(entity: LocationChange): Boolean = query.contains(entity.changeAt)

	override def isPartitionThatMatchQuery(query: Period)(p: FilePartition[Day]): Boolean = query.overlapWith(p.key)

	// 以下使用しないため未実装
	override def get(id: LocationChange => Boolean): Task[Option[LocationChange]] = ???

	override def remove(id: LocationChange => Boolean): Task[(this.type, LocationChange)] = ???

	override def putMulti(entities: LocationChange*): Task[(this.type, Seq[LocationChange])] = ???

}


case class LocationChangeFilePartition private[locationchange](name: String, key: Day) extends FilePartition[Day]

class LocationChangeFilePartitionFactory(implicit val ctx: ContextWrapper) extends FilePartitionFactory[Day, LocationChangeFilePartition] {
	val translator = LocationChangeFilePartitionTranslator

	override def byKey(key: Day): LocationChangeFilePartition = translator.reverse(key) match {
		case Some(name) => new LocationChangeFilePartition(name, key)
		case None => throw new IllegalArgumentException("The value '" + key + "' does not translate into partition name")
	}

	override def byName(name: String): Option[LocationChangeFilePartition] = translator(name).map {
		new LocationChangeFilePartition(name, _)
	}
}

case object LocationChangeFilePartitionTranslator
	extends FilePartitionTranslatorByDay(prefix = classOf[LocationChange].getSimpleName)
