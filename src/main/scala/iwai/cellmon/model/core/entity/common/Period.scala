package iwai.cellmon.model.core.entity.common

import java.util.{Calendar, Date}

import iwai.support.android.Implicits._

import scala.concurrent.duration._

sealed trait Period {

	def startAt: Date

	def duration: Duration

	def endAt: Date

	def contains(d: Date): Boolean = startAt <= d && d < endAt

	def contains(p: Period): Boolean = contains(p.startAt) || contains(p.endAt)

	def overlapWith(period: Period): Boolean = period match {
		case p if contains(p) => true
		case p if p.startAt < startAt && endAt < p.endAt => true
		case _ => false
	}
}

trait InfinitePeriod extends Period {
	override val duration = Duration.Inf
}

trait FinitePeriod extends Period {
	lazy val endAt = new Date(startAt.getTime + duration.toMillis)
}

object Period {
	def apply(at: Date, dur: FiniteDuration): Period = {
		new FinitePeriod {
			override val startAt = at
			override val duration = dur
		}
	}

	def to(border: Date): Period = new InfinitePeriod {
		override val startAt = new Date(0L)
		override val endAt = border
	}

	def from(border: Date): Period = new InfinitePeriod {
		val startAt: Date = border
		val endAt: Date = new Date(Long.MaxValue)
	}

	trait Implicits {

		implicit class DateWrapper(val d: Date) {
			def toDay: Day = Day(d)
			def toHour: Hour = Hour(d)
		}

	}

	object Implicits extends Implicits

}


case class Hour(year: Int, month: Int, day: Int, hour: Int) extends FinitePeriod {
	val duration: Duration = 1.hour

	def startAt: Date = {
		val c = Calendar.getInstance()
		c.set(year, month - 1, day, hour, 0, 0)
		c.set(Calendar.MILLISECOND, 0)
		c.getTime
	}
}

case class Day(year: Int, month: Int, day: Int) extends FinitePeriod {
	val duration: Duration = 1.day

	def startAt: Date = Hour(year, month, day, 0).startAt
}


case object Hour {
	def apply(dateWithTime: Date): Hour = {
		val c = Calendar.getInstance()
		c.setTime(dateWithTime)
		apply(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DATE), c.get(Calendar.HOUR_OF_DAY))
	}

	def apply(): Hour = apply(new Date())

	def now(): Hour = apply(new Date())
}

case object Day {
	def apply(dateWithTime: Date): Day = {
		val c = Calendar.getInstance()
		c.setTime(dateWithTime)
		apply(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DATE))
	}

	def apply(): Day = today()

	def today(): Day = apply(new Date())
}


