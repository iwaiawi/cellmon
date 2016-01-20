/*
 * Copyright (C) 2015 47 Degrees, LLC http://47deg.com hello@47deg.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may
 *  not use this file except in compliance with the License. You may obtain
 *  a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package iwai.cellmon.ui.fragment.cells

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.{LinearLayoutManager, RecyclerView}
import android.view.View.OnClickListener
import android.view.{LayoutInflater, View, ViewGroup}
import com.fortysevendeg.macroid.extras.RecyclerViewTweaks._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.UIActionsExtras._
import iwai.cellmon.model.core.entity.cell.CellChange
import iwai.cellmon.model.core.entity.common.Day
import iwai.cellmon.model.core.service.CellChangeService
import iwai.cellmon.model.support.android.repository.cellchange.CellChangeFileRepository
import iwai.cellmon.ui.common.ListLayout
import iwai.cellmon.ui.common.LineItemDecorator
import macroid.FullDsl._
import macroid.{ActivityContextWrapper, ContextWrapper, Contexts, Ui}
import timber.log.Timber

import scala.annotation.tailrec
import scalaz.concurrent.Task
import scalaz.{-\/, \/-}

class CellsFragment
  extends Fragment
  with Contexts[Fragment]
  with ListLayout {

  /*override*/ lazy val contextProvider: ContextWrapper = fragmentContextWrapper

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    content
  }

  override def onViewCreated(view: View, savedInstanceState: Bundle): Unit = {
    super.onViewCreated(view, savedInstanceState)
    runUi(
      (recyclerView
        <~ rvLayoutManager(new LinearLayoutManager(fragmentContextWrapper.application))
        <~ rvAddItemDecoration(new LineItemDecorator())) ~
        loadSponsors() ~
        (reloadButton <~ On.click(loadSponsors(forceDownload = true))))
  }

  def loadSponsors(forceDownload: Boolean = false): Ui[_] = {
    val repo = new CellChangeFileRepository
    val task: Task[Seq[CellChange]] = CellChangeService(repo).search(Day.today)
    task.map { result =>
      result.reverse
    }.runAsync {
      case \/-(changes) => runUi(reloadList(changes))
      case -\/(e) => runUi {
        Timber.e(e, e.getMessage)
        failed()
      }
    }

    //    loadSelectedConference(forceDownload) mapUi {
    //      conference =>
    //        reloadList(conference.sponsors)
    //    } recoverUi {
    //      case _ => failed()
    //    }
    loading()
  }

  def reloadList(changes: Seq[CellChange]): Ui[_] = {
    changes.length match {
      case 0 => empty()
      case _ =>
        val changeItems = CellChangeViewItem.from(changes)
        val changeAdapter = new CellChangesAdapter(changeItems, new RecyclerClickListener {
          override def onClick(changeItem: CellChangeViewItem): Unit = {
            changeItem.change map {
              change =>
                //                analyticsServices.sendEvent(
                //                  screenName = Some(analyticsSponsorsScreen),
                //                  category = analyticsCategoryNavigate,
                //                  action = analyticsSponsorsActionGoToSponsor)
                //                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(sponsor.url)))
                runUi(uiShortToast(change.toString))
            }
          }
        })
        adapter(changeAdapter)
    }
  }
}

case class CellChangeViewItem(
                               isHeader: Boolean,
                               header: Option[String],
                               change: Option[CellChange])

object CellChangeViewItem {

  def from(changes: Seq[CellChange]): Seq[CellChangeViewItem] = {

    @tailrec
    def loop(changes: Seq[CellChange], acc: Seq[CellChangeViewItem] = Nil): Seq[CellChangeViewItem] =
      changes match {
        case Nil => acc
        case h :: t =>
          //          loop(t, (acc :+ CellChangeViewItem(isHeader = true, header = Some(h.name), sponsor = None)) ++
          //            h.sponsors.map(sponsor => CellChangeViewItem(isHeader = false, header = None, sponsor = Some(sponsor))))
          loop(t, (acc :+ CellChangeViewItem(isHeader = false, header = None, change = Option(h))))
      }

    loop(changes)

  }

}


class CellChangesAdapter(changeItems: Seq[CellChangeViewItem], listener: RecyclerClickListener)
                        (implicit context: ActivityContextWrapper)
  extends RecyclerView.Adapter[ViewHolderCellChangesAdapter] {

  val recyclerClickListener = listener


  //  override def onCreateViewHolder(parentViewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder = {
  //    val adapter = new SpeakersLayoutAdapter()
  //    adapter.content.setOnClickListener(new OnClickListener {
  //      override def onClick(v: View): Unit = recyclerClickListener.onClick(speakers(v.getTag.asInstanceOf[Int]))
  //    })
  //    new ViewHolderSpeakersAdapter(adapter)
  //  }
  //
  //  override def getItemCount: Int = speakers.size
  //
  //  override def onBindViewHolder(viewHolder: ViewHolderSpeakersAdapter, position: Int): Unit = {
  //    val speaker = speakers(position)
  //    val avatarSize = context.application.getResources.getDimensionPixelSize(R.dimen.size_avatar)
  //    viewHolder.content.setTag(position)
  //    runUi(
  //      (viewHolder.avatar <~
  //        (speaker.picture map {
  //          roundedImage(_, R.drawable.placeholder_circle, avatarSize, Some(R.drawable.placeholder_avatar_failed))
  //        } getOrElse ivSrc(R.drawable.placeholder_avatar_failed))) ~
  //        (viewHolder.name <~ tvText(speaker.name)) ~
  //        (viewHolder.twitter <~ speaker.twitter.map(tvText(_) + vVisible).getOrElse(vGone)) ~
  //        (viewHolder.bio <~ tvText(speaker.bio))
  //    )
  //  }


  override def onCreateViewHolder(parentViewGroup: ViewGroup, i: Int): ViewHolderCellChangesAdapter = {
    val adapter = new CellChangesLayoutAdapter()
    adapter.content.setOnClickListener(new OnClickListener {
      override def onClick(v: View): Unit = recyclerClickListener.onClick(changeItems(v.getTag.asInstanceOf[Int]))
    })
    new ViewHolderCellChangesAdapter(adapter)

    //    viewType match {
    //      case `itemViewTypeSponsor` =>
    //        val adapter = new SponsorsLayoutAdapter()
    //        adapter.content.setOnClickListener(new OnClickListener {
    //          override def onClick(v: View): Unit = recyclerClickListener.onClick(sponsorItems(v.getTag.asInstanceOf[Int]))
    //        })
    //        new ViewHolderCellChangesAdapter(adapter)
    //      case `itemViewTypeHeader` =>
    //        val adapter = new HeaderLayoutAdapter()
    //        adapter.content.setOnClickListener(new OnClickListener {
    //          override def onClick(v: View): Unit = recyclerClickListener.onClick(sponsorItems(v.getTag.asInstanceOf[Int]))
    //        })
    //        new ViewHolderHeaderAdapter(adapter)
    //    }

  }

  override def getItemCount: Int = changeItems.size

  override def onBindViewHolder(viewHolder: ViewHolderCellChangesAdapter, position: Int): Unit = {
    val item = changeItems(position)
    viewHolder.content.setTag(position)
    runUi {
      (viewHolder.changeAt <~ tvText(item.change.get.changeAt.toLocaleString)) ~
        (viewHolder.cell <~ tvText(item.change.get.cell.toString))
    }

    //    getItemViewType(position) match {
    //      case `itemViewTypeSponsor` =>
    //        val vh = viewHolder.asInstanceOf[ViewHolderCellChangesAdapter]
    //        sponsorItem.sponsor map {
    //          sponsor =>
    //            vh.content.setTag(position)
    //            runUi(vh.logo <~ srcImage(sponsor.logo))
    //        }
    //      case `itemViewTypeHeader` =>
    //        val vh = viewHolder.asInstanceOf[ViewHolderHeaderAdapter]
    //        runUi(
    //          vh.headerName <~ sponsorItem.header.map(tvText(_) + vVisible).getOrElse(vGone)
    //        )
    //    }
  }

  //  override def getItemViewType(position: Int): Int = if (sponsorItems(position).isHeader) itemViewTypeHeader else itemViewTypeSponsor

}

//object CellChangesAdapter {
//  val itemViewTypeHeader = 0
//  val itemViewTypeSponsor = 1
//}

trait RecyclerClickListener {
  def onClick(changeItem: CellChangeViewItem)
}