package iwai.cellmon.model.support.android.repository

import java.io._

import iwai.cellmon.model.core.repository.{PartitionFactory, PartitionTranslator, Partition, Repository}
import macroid.ContextWrapper

trait FileRepository[I, E/*, R <: FileRepository[I, E, R]*/] {
	self: Repository[I, E/*, R*/] =>

	import scala.language.reflectiveCalls

	protected def using[B, C <: {def close() : Unit}](closeable: => C)(f: C => B): B = try {
		f(closeable)
	} finally {
		closeable.close()
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

trait FilePartitionFactory[A, P <: FilePartition[A]] extends PartitionFactory[A, P] {
	val ctx: ContextWrapper

	def getAll(): Seq[P] = {
		ctx.bestAvailable.fileList()
				.flatMap(byName)
				.distinct
				.toList
	}
}

abstract class FilePartitionTranslator[A] extends PartitionTranslator[A]

