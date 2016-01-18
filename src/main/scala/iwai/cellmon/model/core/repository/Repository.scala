package iwai.cellmon.model.core.repository

import scalaz.concurrent.Task

trait Repository[I, E/*, R <: Repository[I, E, R]*/] {
	def get(id: I): Task[Option[E]]

	// 引数はGenericなI型とするためボツ
	//	def get(id: E => Boolean): Task[Option[E]] = {
	//		scan().map(_.find(id))
	//	}

	// 内部で使用する以外にあまり使わないのでボツ
	//	def scan(): Task[Seq[E]]

	def put(entity: E): Task[(this.type, E)]

	def remove(id: I): Task[(this.type, E)]
}

// getの順序制御のため作ったが使いづらくなるのでボツ
//trait SortableRepository[I, E, R <: SortableRepository] {
//	self: Repository[I, E, R] =>
//	def get(id: E => Boolean, sortWith: Option[(E, E) => Boolean]): Task[Option[E]] = (sortWith match {
//		case Some(lt) => scan().map(_.sortWith(lt))
//		case None => scan()
//	}).map(_.find(id))
//}

// MultiOpRepositoryに統合
//trait QueriableRepository[I, E, R <: QueriableRepository] {
//	self: Repository[I, E, R] =>
//	def query(q: E => Boolean): Task[Seq[E]] = scan().map(_.filter(q))
//}

trait MultiOpRepository[Q, I, E/*, R <: MultiOpRepository[Q, I, E, R]*/] {
	self: Repository[I, E/*, R*/] =>
	def getMulti(query: Q): Task[Seq[E]]

	def putMulti(enitities: E*): Task[(this.type, Seq[E])]

	// 途中で失敗した場合のトランザクション制御が聞かないためボツ
	//	def putMulti(enitities: E*): Task[(Repository[I, E, R], Seq[E])] = {
	//		Task.gatherUnordered(enitities.map(e => put(e))) map { gathered =>
	//			gathered.foldLeft[(Repository[I, E, R], Seq[E])]((this, Nil))((z, n) => (n._1, z._2 :+ n._2))
	//		}
	//	}

	def removeMulti(query: Q): Task[(this.type, Seq[E])]
}

//trait RemovableRepository[I, E, R <: RemovableRepository] {
//	self: Repository[I, E, R] =>
//	def remove(i: I): Task[(Repository[I, E, R], E)]
//}

trait UpdatableRepository[I, E/*, R <: UpdatableRepository[I, E, R]*/] {
	self: Repository[I, E/*, R*/] =>
	def update(id: E => Boolean, entity: E): Task[(this.type, E)]
}


trait Partition[A] {
	val value: A
	val name: String
}

trait PartitionFactory[A, P <: Partition[A]] {
	val translator: PartitionTranslator[A]

	def byName(name: String): Option[P]

	def byValue(value: A): P
}

abstract class PartitionTranslator[A] extends Function1[String, Option[A]] {
	def reverse(name: A): Option[String]
}




