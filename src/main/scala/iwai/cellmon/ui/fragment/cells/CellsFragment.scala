package iwai.cellmon.ui.fragment.cells

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.{LinearLayoutManager, RecyclerView}
import android.view.View.OnClickListener
import android.view.{LayoutInflater, View, ViewGroup}
import android.widget.TextView
import com.fortysevendeg.macroid.extras.RecyclerViewTweaks._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.UIActionsExtras._
import iwai.cellmon.model.core.entity.cell._
import iwai.cellmon.model.core.entity.common.{Hour, Period, Day}
import iwai.cellmon.model.core.service.CellChangeService
import iwai.cellmon.model.support.android.repository.cellchange.CellChangeFileRepository
import iwai.cellmon.ui.common.{LineItemDecorator, ListLayout}
import iwai.support.macroid.Implicits._
import iwai.support.macroid.DateTimeTextViewTweaks._
import iwai.support.android.Implicits._
import macroid.FullDsl._
import macroid._
import timber.log.Timber

import scala.annotation.tailrec
import scalaz.concurrent.Task
import scalaz.{\/, -\/, \/-}

class CellsFragment
	extends Fragment
	with Contexts[Fragment]
	with ListLayout {

	//  /*override*/ lazy val contextProvider: ContextWrapper = fragmentContextWrapper

	override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
		//    content
		contentWithSwipeRefresh
	}

	override def onViewCreated(view: View, savedInstanceState: Bundle): Unit = {
		super.onViewCreated(view, savedInstanceState)

		//		val defaultPeriod = Day.today
		val defaultPeriod = Hour.now // for debug

		runUi(
			(recyclerView
				<~ rvLayoutManager(new LinearLayoutManager(fragmentContextWrapper.application))
				<~ rvAddItemDecoration(new LineItemDecorator())) ~
				//					loadCellChanges(defaultPeriod) ~
				loading ~
				(refreshLayout <~ srlOnRefreshListener(loadCellChangesAsync(Hour.now, swipe = true))) ~
				(reloadButton <~ On.click(loading() ~ loadCellChangesAsync(Hour.now)))
		)

		loadCellChangesAsync(Hour.now)
	}

	// periodは呼ばれた際に評価する(呼び出す毎に評価されるので注意)
	def loadCellChangesAsync(period: => Period, swipe: Boolean = false): Ui[_] = {
		val repo = new CellChangeFileRepository
		val task: Task[Seq[CellChange]] = CellChangeService(repo).search(period)
		task.map(_.reverse)
			.map(reloadList)
			.map(_ ~ (if(swipe) refreshLayout <~ srlRefreshing(false) else Ui.nop))
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
				val changesAdapter = new CellChangesAdapter(models, RecyclerClickListener.onClick {
					model: CellChangeViewModel =>
						runUi(uiShortToast(model.change.toString))
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


class CellChangesAdapter(items: Seq[CellChangeViewModel], listener: RecyclerClickListener[CellChangeViewModel])
	(implicit context: ActivityContextWrapper)
	extends RecyclerView.Adapter[CellChangeViewHolder] with AdapterStyles {

	val recyclerClickListener = listener

	override def onCreateViewHolder(parentViewGroup: ViewGroup, i: Int): CellChangeViewHolder = {
		val itemLayout = new CellChangeItemLayout()
		itemLayout.itemContent <~ On.click {

		}


		//		itemLayout.content.setOnClickListener { v: View =>
		//			recyclerClickListener.onClick(items(v.getTag.asInstanceOf[Int]))
		//		}

		new CellChangeViewHolder(itemLayout)
	}

	override def getItemCount: Int = items.size

	override def onBindViewHolder(viewHolder: CellChangeViewHolder, position: Int): Unit = {
		viewHolder.content.setTag(position)
		val item = items(position)

		runUi {
			(viewHolder.changeAt <~ tvDate(item.change.changeAt)) ~
				(viewHolder.cell <~ tvCell(item.change.cell))
		}
	}
}

trait RecyclerClickListener[M] {
	def onClick(model: M): Unit = {}

	def onLongClick(model: M): Unit = {}

}

object RecyclerClickListener {

	implicit def onClick[M](f: M => _) = new RecyclerClickListener[M] {
		override def onClick(model: M): Unit = f(model)
	}

	implicit def onLongClick[M](f: M => _) = new RecyclerClickListener[M] {
		override def onLongClick(model: M): Unit = f(model)
	}
}