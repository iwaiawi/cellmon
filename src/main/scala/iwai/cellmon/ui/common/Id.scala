package iwai.cellmon.ui.common

// Android resource id class using phantom type
case class Id[A <: Id.R](value: Int) extends AnyVal

case object Id {
	implicit def id2Int[A <: R](id: Id[A]): Int = id.value

	implicit def int2Id[A <: R](i: Int): Id[A] = Id[A](i)

	sealed trait Resource

	type R = Resource

	trait StringR extends R

	trait IntegerR extends R

	trait BooleanR extends R

	trait DrawableR extends R

	trait ColorR extends R

	trait DimenR extends R

}

