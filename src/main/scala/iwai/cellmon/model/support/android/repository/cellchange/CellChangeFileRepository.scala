package iwai.cellmon.model.support.android.repository.cellchange

import iwai.cellmon.model.core.entity.cell._
import iwai.cellmon.model.core.entity.common.Period
import iwai.cellmon.model.core.repository.cellchange._
import iwai.cellmon.model.support.android.repository._

import iwai.cellmon.model.core.entity.common._
import iwai.cellmon.model.core.entity.common.Period.Implicits._
import macroid.ContextWrapper
import spray.json._

import scalaz.concurrent.Task

import fommil.sjs.FamilyFormats._


class CellChangeFileRepository(implicit val ctx: ContextWrapper)
	extends CellChangeRepository
		with FileRepository[CellChange => Boolean, CellChange, Day]
		with MultiOpFileRepository[Period, CellChange => Boolean, CellChange, Day] {

	implicit val partitionFactory = new CellChangeFilePartitionFactory

	override def entityToJson(entity: CellChange): JsValue = entity.toJson

	override def jsonToEntity(jsonString: String): CellChange = jsonString.parseJson.convertTo[CellChange]

	override def entityToPartitionKey(entity: CellChange): Day = entity.changeAt.toDay

	override def isEntityMatchedByQuery(query: Period)(entity: CellChange): Boolean = query.contains(entity.changeAt)

	override def isPartitionMatchedByQuery(query: Period)(p: FilePartition[Day]): Boolean = query.overlapWith(p.key)

	//	override def put(entity: CellChange): Task[(this.type, CellChange)] = Task {
	//		val writer = partitionFactory.byValue(entity.changeAt.toDay).writer(Context.MODE_PRIVATE | Context.MODE_APPEND)
	//		using(writer)(_.append(entity.toJson.compactPrint + "\n"))
	//		(this, entity)
	//	}

	//	override def getMulti(period: Period): Task[Seq[CellChange]] = Task {
	//		(for {
	//			partition <- partitionFactory.getAll().filter(_.key.overlapWith(period))
	//			reader <- partition.reader
	//		} yield {
	//			using(reader) { r =>
	//				Iterator.continually(r.readLine()).takeWhile(_ != null).map {
	//					_.parseJson.convertTo[CellChange]
	//				}.filter { change =>
	//					period.contains(change.changeAt)
	//				}.toList
	//			}
	//		}).flatten.sortBy(_.changeAt)
	//	}
	//
	//	override def removeMulti(period: Period): Task[(this.type, Seq[CellChange])] = Task {
	//		val removed = partitionFactory.getAll() /*.par*/ .filter(_.key.overlapWith(period)).flatMap { p =>
	//			p.reader.map { reader =>
	//				val (toRemove, toLeft) = using(reader) { r =>
	//					Iterator.continually(r.readLine()).takeWhile(_ != null).map { jsonString =>
	//						(jsonString, jsonString.parseJson.convertTo[CellChange])
	//					}.toList
	//				}.partition { jsonAndEntity =>
	//					period.contains(jsonAndEntity._2.changeAt)
	//				}
	//
	//				if(toLeft.isEmpty) {
	//					ctx.bestAvailable.deleteFile(p.name)
	//				} else {
	//					using(p.writer(Context.MODE_PRIVATE)) {
	//						_.append(toLeft.map(_._1).mkString("", "\n", "\n"))
	//					}
	//				}
	//				toRemove.map(_._2)
	//			}
	//		}.flatten.sortBy(_.changeAt)
	//
	//		(this, removed)
	//	}

	// 以下使用しないため未実装
	override def get(id: CellChange => Boolean): Task[Option[CellChange]] = ???

	override def remove(id: CellChange => Boolean): Task[(this.type, CellChange)] = ???

	override def putMulti(entities: CellChange*): Task[(this.type, Seq[CellChange])] = ???

}


case class CellChangeFilePartition private[cellchange](name: String, key: Day) extends FilePartition[Day]

class CellChangeFilePartitionFactory(implicit val ctx: ContextWrapper) extends FilePartitionFactory[Day, CellChangeFilePartition] {
	val translator = CellChangeFilePartitionTranslator

	override def byKey(key: Day): CellChangeFilePartition = translator.reverse(key) match {
		case Some(name) => new CellChangeFilePartition(name, key)
		case None => throw new IllegalArgumentException("The value '" + key + "' does not translate into partition name")
	}

	override def byName(name: String): Option[CellChangeFilePartition] = translator(name).map {
		new CellChangeFilePartition(name, _)
	}
}

case object CellChangeFilePartitionTranslator
	extends FilePartitionTranslatorByDay(prefix = classOf[CellChange].getSimpleName)
