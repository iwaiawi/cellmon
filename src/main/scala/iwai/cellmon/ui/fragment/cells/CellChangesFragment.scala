package iwai.cellmon.ui.fragment.cells

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.{LayoutInflater, View, ViewGroup}
import com.fortysevendeg.macroid.extras.RecyclerViewTweaks._
import com.fortysevendeg.macroid.extras.UIActionsExtras._
import iwai.cellmon.model.core.entity.cell._
import iwai.cellmon.model.core.entity.common.{Day, Period}
import iwai.cellmon.model.core.service.CellChangeService
import iwai.cellmon.model.support.android.repository.cellchange.CellChangeFileRepository
import iwai.cellmon.ui.common.{LineItemDecorator, ListLayout}
import iwai.support.macroid.Implicits._
import macroid.FullDsl._
import macroid._
import timber.log.Timber

import scalaz.concurrent.Task
import scalaz.{-\/, \/-}

class CellChangesFragment
  extends Fragment
    with Contexts[Fragment]
    with ListLayout {

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    getUi(layoutWithSwipeRefresh)
  }

  override def onViewCreated(view: View, savedInstanceState: Bundle): Unit = {
    super.onViewCreated(view, savedInstanceState)

    val period: () => Period = Day.today
    //    val period: () => Period = Hour.now // for debug

    runUi(
      (recyclerView
        <~ rvLayoutManager(new LinearLayoutManager(fragmentContextWrapper.application))
        <~ rvAddItemDecoration(new LineItemDecorator())) ~
        loading ~
        (refreshLayout <~ On.refresh[SwipeRefreshLayout] {
          loadCellChangesAsync(period(), swipe = true)
        }) ~
        (reloadButton <~ On.click {
          loading() ~ loadCellChangesAsync(period())
        })
    )

    loadCellChangesAsync(period())
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

    Ui.nop
  }

  def reloadList(changes: Seq[CellChange]): Ui[_] = {
    changes.isEmpty match {
      case true => empty()
      case false =>
        val models = CellChangeViewModel.from(changes)
        val changesAdapter = new CellChangesAdapter(models, afterCreateViewHolder = { vh =>
          vh.slots.content <~ FuncOn.click { v: View =>
            val model = models(vh.getAdapterPosition)
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
