/*
 * Copyright (C) 2015 47 Degrees, LLC http://47deg.com hello@47deg.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package iwai.cellmon.ui.fragment.menu

import android.app.Activity
import android.content.{Context, Intent}
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.{LayoutInflater, View, ViewGroup}
import com.fortysevendeg.macroid.extras.RecyclerViewTweaks._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import iwai.cellmon.R
import iwai.cellmon.ui.activity.main.MainActivity
import macroid.FullDsl._
import macroid._
import com.fortysevendeg.macroid.extras.RecyclerViewTweaks._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ImageViewTweaks._

import scala.concurrent.ExecutionContext.Implicits.global

class MenuFragment
  extends Fragment
  with Contexts[Fragment]
//  with ComponentRegistryImpl
//  with UiServices
  with IdGeneration
  with Layout {

//  /*override */implicit lazy val contextProvider: ContextWrapper = fragmentContextWrapper

  val defaultItem = 0

  private var mainActivity: Option[MainActivity] = None

//  private var urlTickets: Option[String] = None

  private val previousItemSelectedKey = "previous_item_selected_key"

  lazy val mainMenuAdapter: MainMenuAdapter = new MainMenuAdapter(new MainMenuClickListener {
    override def onClick(mainMenuItem: MainMenuItem) =
      mainMenuItem.section match {
//        case MenuSection.TICKETS =>
////          analyticsServices.sendEvent(
////            screenName = None,
////            category = analyticsCategoryNavigate,
////            action = analyticsActionGoToTickets)
//          urlTickets map {
//            url =>
//              val intent = new Intent(Intent.ACTION_VIEW,
//                Uri.parse(url))
//              fragmentContextWrapper.getOriginal.startActivity(intent)
//          }
        case _ => itemSelected(mainMenuItem)
      }
  })

//  private var mainMenuVisible: Boolean = true

//  override def onAttach(activity: Activity) = {
//    super.onAttach(activity)
//    mainActivity = Some(activity.asInstanceOf[MainActivity])
//  }

  override def onAttach(context: Context) = {
    super.onAttach(context)
    mainActivity = Some(context.asInstanceOf[MainActivity])
  }

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    val root = content
    runUi(
      recyclerView <~ rvLayoutManager(new LinearLayoutManager(fragmentContextWrapper.application))
    )
    root
  }

  override def onViewCreated(view: View, savedInstanceState: Bundle) = {
    super.onViewCreated(view, savedInstanceState)
    runUi(
      (recyclerView <~ rvAdapter(mainMenuAdapter)) /*~
        (bigImageLayout <~ On.click {
          Ui {
            toggleMenu()
          }
        })*/
    )
    val defaultSection = Option(savedInstanceState) map (_.getInt(previousItemSelectedKey, defaultItem)) getOrElse defaultItem
    itemSelected(mainMenuAdapter.list(defaultSection), savedInstanceState == null)

//    val result = for {
//      conferences <- loadConferences()
//      selectedConference <- findConference(conferences, loadSelectedConferenceId)
//    } yield {
//      showConference(selectedConference.info)
//      urlTickets = Some(selectedConference.info.registrationSite)
//    }
//
//    result.recover {
//      case _ => failed()
//    }
  }

//  def failed() = {}


  override def onSaveInstanceState(outState: Bundle): Unit = {
    outState.putInt(previousItemSelectedKey, mainMenuAdapter.selectedItem map (mainMenuAdapter.list.indexOf(_)) getOrElse defaultItem)
    super.onSaveInstanceState(outState)
  }

  override def onDetach(): Unit = {
    mainActivity = None
    super.onDetach()
  }

//  def toggleMenu() = {
//    mainMenuVisible = !mainMenuVisible
//    if (mainMenuVisible) runUi(
//      (conferenceSelector <~ ivSrc(R.drawable.menu_header_select_arrow)) ~
//        (recyclerView <~ rvAdapter(mainMenuAdapter)))
//    else for {
//      conferences <- loadConferences()
//    } yield {
//      conferenceMenuAdapter.loadConferences(conferences map (_.info))
//      runUi(
//        (conferenceSelector <~ ivSrc(R.drawable.menu_header_select_arrow_up)) ~
//          (recyclerView <~ rvAdapter(conferenceMenuAdapter)))
//    }
//  }

//  def showMainMenu =
//    if (!mainMenuVisible) toggleMenu()

  def itemSelected(menuItem: MainMenuItem, callCallback: Boolean = true) = {
    mainMenuAdapter.selectItem(Some(menuItem))
    if (callCallback) mainActivity map (_.itemSelected(menuItem.section, menuItem.name))
  }

}
