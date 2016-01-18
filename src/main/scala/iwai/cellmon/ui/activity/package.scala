package iwai.cellmon

import macroid.Ui

package object activities {
	import scala.language.implicitConversions
	implicit def unit2Ui(unit: Unit): Ui[Unit] = Ui.nop
}
