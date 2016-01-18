package iwai.cellmon.model.support.android.repository.cellchange

import java.util.regex.Pattern

import android.content.Context
import iwai.cellmon.model.core.entity.cell._
import iwai.cellmon.model.core.entity.common.Period
import iwai.cellmon.model.core.repository.cellchange._
import iwai.cellmon.model.support.android.repository.{FilePartition, FilePartitionFactory, FilePartitionTranslator, FileRepository}

import iwai.cellmon.model.core.entity.common._
import iwai.cellmon.model.core.entity.common.Period.Implicits._
import macroid.ContextWrapper
import spray.json._

import scalaz.concurrent.Task

import fommil.sjs.FamilyFormats._

class CellChangeFileRepository(implicit val ctx: ContextWrapper) extends CellChangeRepository with FileRepository[ID, ENTITY /*, CellChangeRepository*/ ] {
	val factory = new CellChangeFilePartitionFactory

	override def put(entity: CellChange): Task[(this.type, CellChange)] = Task {
		val writer = factory.byValue(entity.changeAt.toDay).writer(Context.MODE_PRIVATE | Context.MODE_APPEND)
		using(writer)(_.append(entity.toJson.compactPrint + "\n"))
		(this, entity)
	}

	override def getMulti(period: Period): Task[Seq[CellChange]] = Task {
		(for {
			partition <- factory.getAll().filter(_.value.overlapWith(period))
			reader <- partition.reader
		} yield {
			using(reader) { r =>
				Iterator.continually(r.readLine()).takeWhile(_ != null).map {
					_.parseJson.convertTo[CellChange]
				}.filter { change =>
					period.contains(change.changeAt)
				}.toList
			}
		}).flatten.sortBy(_.changeAt)
	}

	override def removeMulti(period: Period): Task[(this.type, Seq[CellChange])] = Task {
		val removed = factory.getAll()/*.par*/.filter(_.value.overlapWith(period)).flatMap { p =>
			p.reader.map { reader =>
				val (toRemove, toLeft) = using(reader) { r =>
					Iterator.continually(r.readLine()).takeWhile(_ != null).map { jsonString =>
						(jsonString, jsonString.parseJson.convertTo[CellChange])
					}.toList
				}.partition { jsonAndEntity =>
					period.contains(jsonAndEntity._2.changeAt)
				}

				if(toLeft.isEmpty) {
					ctx.bestAvailable.deleteFile(p.name)
				} else {
					using(p.writer(Context.MODE_PRIVATE)) {
						_.append(toLeft.map(_._1).mkString("", "\n", "\n"))
					}
				}
				toRemove.map(_._2)
			}
		}.flatten.sortBy(_.changeAt)

		(this, removed)
	}

	// 以下使用しないため未実装
	override def get(id: CellChange => Boolean): Task[Option[CellChange]] = ???

	override def remove(id: CellChange => Boolean): Task[(this.type, CellChange)] = ???

	override def putMulti(enitities: CellChange*): Task[(this.type, Seq[CellChange])] = ???
}


case class CellChangeFilePartition private[cellchange](name: String, value: Day) extends FilePartition[Day]

class CellChangeFilePartitionFactory(implicit val ctx: ContextWrapper) extends FilePartitionFactory[Day, CellChangeFilePartition] {
	val translator = CellChangeFilePartitionTranslator

	override def byValue(value: Day): CellChangeFilePartition = translator.reverse(value) match {
		case Some(name) => new CellChangeFilePartition(name, value)
		case None => throw new IllegalArgumentException("The value '" + value + "' does not translate into partition name")
	}

	override def byName(name: String): Option[CellChangeFilePartition] = translator(name).map {
		new CellChangeFilePartition(name, _)
	}
}

case object CellChangeFilePartitionTranslator extends FilePartitionTranslator[Day] {

	private val prefix = classOf[CellChange].getSimpleName

	override def apply(name: String): Option[Day] = {
		val regex = (Pattern.quote(prefix) + """_(\d{4})(\d{2})(\d{2})\.data$""").r
		name match {
			case regex(year, month, day) => Option(Day(year.toInt, month.toInt, day.toInt))
			//			case _ => throw new IllegalArgumentException("This name '" + name + "' does not translate into Day.")
			case _ => None
		}
	}

	override def reverse(d: Day): Option[String] = {
		Option("%s_%tY%<tm%<td.data".format(prefix, d.startAt))
	}
}
