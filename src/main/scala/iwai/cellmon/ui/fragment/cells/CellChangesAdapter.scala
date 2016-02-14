package iwai.cellmon.ui.fragment.cells

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import iwai.support.macroid.DateTimeTextViewTweaks._
import macroid.FullDsl._
import macroid.{ActivityContextWrapper, Ui}


class CellChangesAdapter(
  viewModels: Seq[CellChangeViewModel],
  afterCreateViewHolder: CellChangeViewHolder => Ui[_] = (_ => Ui.nop)
)(implicit context: ActivityContextWrapper)
  extends RecyclerView.Adapter[CellChangeViewHolder] with AdapterStyles {

  override def onCreateViewHolder(parentViewGroup: ViewGroup, i: Int): CellChangeViewHolder = {
    val viewHolder = new CellChangeViewHolder(new CellChangeItemLayout)
    runUi(afterCreateViewHolder(viewHolder))
    viewHolder
  }

  override def getItemCount: Int = viewModels.size

  override def onBindViewHolder(viewHolder: CellChangeViewHolder, position: Int): Unit = {
    val model = viewModels(position)

    runUi {
      (viewHolder.slots.changeAt <~ tvTimestamp(model.change.changeAt)) ~
        (viewHolder.slots.cell <~ tvCell(model.change.cell))
    }
  }
}
