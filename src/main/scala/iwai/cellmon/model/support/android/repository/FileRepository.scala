package iwai.cellmon.model.support.android.repository

import java.io._
import java.util.regex.Pattern

import android.content.Context
import iwai.cellmon.model.core.entity.cell.CellChange
import iwai.cellmon.model.core.entity.common.{Period, Day}
import iwai.cellmon.model.core.repository._
import macroid.ContextWrapper

import scala.util.matching.Regex
import spray.json._

import scalaz.concurrent.Task

import fommil.sjs.FamilyFormats._


trait FileRepository[I, E, PK] {
	self: Repository[I, E] =>

	import scala.language.reflectiveCalls

	protected def using[B, C <: {def close() : Unit}](closeable: => C)(f: C => B): B = try {
		f(closeable)
	} finally {
		closeable.close()
	}

	implicit val ctx: ContextWrapper

	def entityToJson(entity: E): JsValue

	def jsonToEntity(jsonString: String): E

	val partitionFactory: FilePartitionFactory[PK, _ <: FilePartition[PK]]

	def entityToPartitionKey(entity: E): PK

	override def put(entity: E): Task[(this.type, E)] = Task {
		val writer = partitionFactory.byKey(entityToPartitionKey(entity)).writer(Context.MODE_PRIVATE | Context.MODE_APPEND)
		using(writer)(_.append(entityToJson(entity).compactPrint + "\n"))
		(this, entity)
	}
}

trait MultiOpFileRepository[Q, I, E, PK] {
	self: MultiOpRepository[Q, I, E] with FileRepository[I, E, PK] =>

	def isPartitionMatchedByQuery(query: Q)(p: FilePartition[PK]): Boolean

	def isEntityMatchedByQuery(query: Q)(entity: E): Boolean

	override def getMulti(query: Q): Task[Seq[E]] = Task {
		(for {
			partition <- partitionFactory.getAll().filter(isPartitionMatchedByQuery(query)(_))
			reader <- partition.reader
		} yield {
			using(reader) { r =>
				Iterator.continually(r.readLine()).takeWhile(_ != null).map { jsonString =>
					jsonToEntity(jsonString)
				}.filter(isEntityMatchedByQuery(query)(_))
					.toList
			}
		}).flatten
	}

	def putMulti(entities: E*): Task[(this.type, Seq[E])]

	override def removeMulti(query: Q): Task[(this.type, Seq[E])] = Task {
		val removed = partitionFactory.getAll() /*.par*/ .filter(isPartitionMatchedByQuery(query)(_)).flatMap { p =>
			p.reader.map { reader =>
				val (toRemove, toLeft) = using(reader) { r =>
					Iterator.continually(r.readLine()).takeWhile(_ != null).map { jsonString =>
						(jsonString, jsonToEntity(jsonString))
					}.toList
				}.partition { jsonAndEntity =>
					isEntityMatchedByQuery(query)(jsonAndEntity._2)
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
		}.flatten

		(this, removed)
	}
}

trait FilePartition[A] extends Partition[A] {
	val charset = "UTF-8"

	def reader(implicit ctx: ContextWrapper): Option[BufferedReader] = {
		try {
			Option(
				new BufferedReader(
					new InputStreamReader(
						ctx.bestAvailable.openFileInput(name),
						charset
					)
				)
			)
		} catch {
			case _: FileNotFoundException => None
		}
	}

	def writer(mode: Int)(implicit ctx: ContextWrapper): Writer = {
		new PrintWriter(
			new OutputStreamWriter(
				ctx.bestAvailable.openFileOutput(name, mode),
				charset
			)
		)
	}
}

trait FilePartitionFactory[PK, P <: FilePartition[PK]] extends PartitionFactory[PK, P] {
	val ctx: ContextWrapper

	def getAll(): Seq[P] = {
		ctx.bestAvailable.fileList()
			.sorted
			.flatMap(byName)
			.distinct
			.toList
	}
}

abstract class FilePartitionTranslator[PK] extends PartitionTranslator[PK]

abstract class FilePartitionTranslatorByDay(prefix: String) extends FilePartitionTranslator[Day] {

	override def apply(name: String): Option[Day] = {
		val regex = (Pattern.quote(prefix) + """_(\d{4})(\d{2})(\d{2})\.data$""").r
		name match {
			case regex(year, month, day) => Option(Day(year.toInt, month.toInt, day.toInt))
			case _ => None
		}
	}

	override def reverse(d: Day): Option[String] = {
		Option("%s_%tY%<tm%<td.data".format(prefix, d.startAt))
	}
}

