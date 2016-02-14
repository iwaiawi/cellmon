package iwai.cellmon.ui.fragment.locations

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import iwai.support.macroid.DateTimeTextViewTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import macroid.FullDsl._
import macroid.{ActivityContextWrapper, Ui}


class LocationChangesAdapter(
  viewModels: Seq[LocationChangeViewModel],
  afterCreateViewHolder: LocationChangeViewHolder => Ui[_] = (_ => Ui.nop)
)(implicit context: ActivityContextWrapper)
  extends RecyclerView.Adapter[LocationChangeViewHolder] with AdapterStyles {

  override def onCreateViewHolder(parentViewGroup: ViewGroup, i: Int): LocationChangeViewHolder = {
    val viewHolder = new LocationChangeViewHolder(new LocationChangeItemLayout)
    runUi(afterCreateViewHolder(viewHolder))
    viewHolder
  }

  override def getItemCount: Int = viewModels.size

  override def onBindViewHolder(viewHolder: LocationChangeViewHolder, position: Int): Unit = {
    val model = viewModels(position)

    runUi {
      (viewHolder.slots.changeAt <~ tvTimestamp(model.change.changeAt)) ~
      (viewHolder.slots.location <~ tvLocation(model.change.location)) ~
        ( model.change.cell.map { cell =>
          (viewHolder.slots.cell <~ tvCell(cell))
        } getOrElse (viewHolder.slots.cell <~ vInvisible))
    }
  }
}
