package iwai.cellmon.ui.fragment.cells

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.{LinearLayoutManager, RecyclerView}
import android.view.{LayoutInflater, View, ViewGroup}
import com.fortysevendeg.macroid.extras.RecyclerViewTweaks._
import com.fortysevendeg.macroid.extras.UIActionsExtras._
import iwai.cellmon.model.core.entity.cell._
import iwai.cellmon.model.core.entity.common.{Day, Period}
import iwai.cellmon.model.core.service.CellChangeService
import iwai.cellmon.model.support.android.repository.cellchange.CellChangeFileRepository
import iwai.cellmon.ui.common.{LineItemDecorator, ListLayout}
import iwai.support.macroid.Implicits._
import iwai.support.macroid.DateTimeTextViewTweaks._
import macroid.FullDsl._
import macroid._
import timber.log.Timber

import scalaz.concurrent.Task
import scalaz.{-\/, \/-}

class CellsFragment
  extends Fragment
    with Contexts[Fragment]
    with ListLayout {

  //  /*override*/ lazy val contextProvider: ContextWrapper = fragmentContextWrapper

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    //    content
    getUi(layoutWithSwipeRefresh)
  }

  override def onViewCreated(view: View, savedInstanceState: Bundle): Unit = {
    super.onViewCreated(view, savedInstanceState)

    val defaultPeriod: () => Period = Day.today
    //    val defaultPeriod: () => Period = Hour.now // for debug

    runUi(
      (recyclerView
        <~ rvLayoutManager(new LinearLayoutManager(fragmentContextWrapper.application))
        <~ rvAddItemDecoration(new LineItemDecorator())) ~
        //					loadCellChanges(defaultPeriod) ~
        loading ~
        (refreshLayout <~ On.refresh[SwipeRefreshLayout](
          loadCellChangesAsync(defaultPeriod(), swipe = true)
        )) ~
        (reloadButton <~ On.click(
          loading() ~ loadCellChangesAsync(defaultPeriod())
        ))
    )

    loadCellChangesAsync(defaultPeriod())
  }

  // periodは呼ばれた際に評価する(呼び出す毎に評価されるので注意)
  def loadCellChangesAsync(period: => Period, swipe: Boolean = false): Ui[Unit] = {
    val repo = new CellChangeFileRepository
    val task: Task[Seq[CellChange]] = CellChangeService(repo).search(period)
    task.map(_.reverse)
      .map(reloadList)
      .map(_ ~ (if (swipe) refreshLayout <~ srlRefreshing(false) else Ui.nop))
      .runAsyncUi {
        case \/-(ui) => ui
        case -\/(e) =>
          Timber.e(e, e.getMessage)
          failed()
      }

    //		loading()
    Ui.nop
  }


  def reloadList(changes: Seq[CellChange]): Ui[_] = {
    changes.isEmpty match {
      case true => empty()
      case false =>
        val models = CellChangeViewModel.from(changes)
        //        val changesAdapter = new CellChangesAdapter(models)
        //          with RecyclerClickableAdapter[CellChangeViewModel, CellChangeViewHolder] {
        //          val listener = RecyclerClickListener.onClick[CellChangeViewModel] {
        //            model => uiShortToast(model.change.toString)
        //          }
        //        }
        //        val changesAdapter = new CellChangesAdapter(models) {
        //          override def onCreateViewHolder(parentViewGroup: ViewGroup, i: Int): CellChangeViewHolder = {
        //            val viewHolder = super.onCreateViewHolder(parentViewGroup, i)
        //            runUi(
        //              viewHolder.slots.content <~ FuncOn.click { v: View =>
        //                val model = models(viewHolder.getAdapterPosition)
        //                uiShortToast(model.change.toString)
        //              }
        //            )
        //
        //            viewHolder
        //          }
        //        }
        val changesAdapter = new CellChangesAdapter(models)({
          viewHolder: CellChangeViewHolder =>
          viewHolder.slots.content <~ FuncOn.click { v: View =>
            val model = models(viewHolder.getAdapterPosition)
            uiShortToast(model.change.toString)
          }
        })

        adapter(changesAdapter)
    }
  }
}


case class CellChangeViewModel(
  change: CellChange
)

object CellChangeViewModel {

  def from(changes: Seq[CellChange]): Seq[CellChangeViewModel] = {
    changes.map(CellChangeViewModel.apply)
  }
}

class CellChangesAdapter(viewModels: Seq[CellChangeViewModel])
  (afterCreateViewHolder: CellChangeViewHolder => Ui[_])
  (implicit context: ActivityContextWrapper)
  extends RecyclerView.Adapter[CellChangeViewHolder]
  with AdapterStyles {

  override def onCreateViewHolder(parentViewGroup: ViewGroup, i: Int): CellChangeViewHolder = {
    //    val itemLayout = new CellChangeItemLayout()
    val viewHolder = new CellChangeViewHolder(new CellChangeItemLayout())
    //    runUi(
    //      viewHolder.slots.content <~ FuncOn.click { v: View =>
    //        listener.onClick(models(v.getTag.asInstanceOf[Int]))
    //      }
    //    )
    runUi(afterCreateViewHolder(viewHolder))
    viewHolder
  }

  override def getItemCount: Int = viewModels.size

  override def onBindViewHolder(viewHolder: CellChangeViewHolder, position: Int): Unit = {
    //    viewHolder.slots.content <~ hold(position) // setTag(position)
    val model = viewModels(position)

    runUi {
      (viewHolder.slots.changeAt <~ tvDate(model.change.changeAt)) ~
        (viewHolder.slots.cell <~ tvCell(model.change.cell))
    }
  }
}


trait RecyclerClickableAdapter[VM, VH <: RecyclerView.ViewHolder with RecyclerClickableViewHolder]
  extends RecyclerView.Adapter[VH] {

  val viewModels: Seq[VM]
  val listener: RecyclerClickListener[VM]

  abstract override def onCreateViewHolder(parentViewGroup: ViewGroup, i: Int): VH = {
    val viewHolder = super.onCreateViewHolder(parentViewGroup, i)
    runUi(
      viewHolder.clickableSlot <~ FuncOn.click { v: View =>
        listener.onClick(viewModels(v.getTag.asInstanceOf[Int]))
      }
    )
    viewHolder
  }

  abstract override def onBindViewHolder(viewHolder: VH, position: Int): Unit = {
    viewHolder.clickableSlot <~ hold(position) // = v.setTag(position)
    super.onBindViewHolder(viewHolder, position)
  }
}

trait RecyclerClickableViewHolder {
  self: RecyclerView.ViewHolder =>
  val clickableSlot: Option[View]
}

trait RecyclerClickListener[M] {
  def onClick(model: M): Ui[_] = Ui.nop

  def onLongClick(model: M): Ui[_] = Ui.nop

}

object RecyclerClickListener {

  import scala.language.implicitConversions

  implicit def onClick[M](f: M => Ui[_]) = new RecyclerClickListener[M] {
    override def onClick(model: M): Ui[_] = f(model)
  }

  implicit def onLongClick[M](f: M => Ui[_]) = new RecyclerClickListener[M] {
    override def onLongClick(model: M): Ui[_] = f(model)
  }
}